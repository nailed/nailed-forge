package jk_5.nailed.map;

import java.io.File;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;

import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.events.MapCreatedEvent;
import jk_5.nailed.api.events.MapRemovedEvent;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapLoader;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.PvpIgnoringDamageSource;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.ipc.mappack.IpcMappackRegistry;

/**
 * {@inheritDoc}
 *
 * @author jk-5
 */
public class NailedMapLoader implements MapLoader {

    private static NailedMapLoader instance;
    private final File mapsFolder = new File("maps");
    private Map lobby;
    private final List<Map> maps = Lists.newArrayList();
    private Random randomSpawnpointSelector = new Random();

    private final BitSet dimensionIds;
    private final Hashtable<Integer, Integer> dimensions;

    @SuppressWarnings("unchecked")
    public NailedMapLoader() {
        instance = this;
        try{
            Field f = DimensionManager.class.getDeclaredField("dimensionMap");
            f.setAccessible(true);
            this.dimensionIds = (BitSet) f.get(null);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        try{
            Field f = DimensionManager.class.getDeclaredField("dimensions");
            f.setAccessible(true);
            this.dimensions = (Hashtable<Integer, Integer>) f.get(null);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public static NailedMapLoader instance() {
        if(NailedAPI.getMapLoader() == null){
            NailedAPI.setMapLoader(new NailedMapLoader());
        }
        return instance;
    }

    @Override
    public void registerMap(@Nonnull Map map) {
        Preconditions.checkNotNull(map, "map");
        if(map.getID() == 0){
            this.lobby = map;
        }
        this.maps.add(map);
        NailedLog.info("Registered {}", map.getSaveFileName());
    }

    @Override
    public void createMapServer(@Nonnull final Mappack mappack, @Nullable final Callback<Map> callback) {
        Preconditions.checkNotNull(mappack, "mappack");
        final PotentialMap potentialMap = new PotentialMap(mappack);
        NailedLog.info("Scheduling the load of {}", potentialMap.getSaveFileName());
        NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run() {
                NailedLog.info("Preparing {}", potentialMap.getSaveFileName());
                mappack.prepareWorld(potentialMap.getSaveFolder(), new Callback<Void>() {
                    @Override
                    public void callback(Void obj) {
                        final Map map = mappack.createMap(potentialMap);
                        NailedAPI.getScheduler().runTask(new NailedRunnable() {
                            @Override
                            public void run() {
                                NailedLog.info("Loading {}", potentialMap.getSaveFileName());
                                map.initMapServer();
                                MinecraftForge.EVENT_BUS.post(new MapCreatedEvent(map));
                                if(callback != null){
                                    callback.callback(map);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    @Nullable
    public Map getMap(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name");
        for(Map map : this.maps){
            if(map.getSaveFileName().equalsIgnoreCase(name)){
                return map;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public Map getMap(int id) {
        if(id == 0){
            return this.getLobby();
        }
        for(Map map : this.maps){
            if(map.getID() == id){
                return map;
            }
        }
        if(DimensionManager.isDimensionRegistered(id)){
            return new NailedMap(null, id);
        }else{
            return null;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public Map getMap(@Nonnull World world) {
        Preconditions.checkNotNull(world, "world");
        return this.getMap(world.provider.dimensionId);
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Map map = this.getMap(event.player.worldObj);
        map.onPlayerJoined(NailedAPI.getPlayerRegistry().getPlayer(event.player));
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Map map = this.getMap(event.player.worldObj);
        map.onPlayerLeft(NailedAPI.getPlayerRegistry().getPlayer(event.player));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldEvent.Load event) {
        this.getMap(event.world).setWorld((WorldServer) event.world);
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player p = NailedAPI.getPlayerRegistry().getPlayer(event.player);
        Map oldMap = this.getMap(event.fromDim);
        Map newMap = this.getMap(event.toDim);
        assert oldMap != null;
        assert newMap != null;
        oldMap.onPlayerLeft(p);
        newMap.onPlayerJoined(p);
        for(Map map : this.maps){
            map.getSignCommandHandler().onPlayerLeftMap(oldMap, p);
            map.getSignCommandHandler().onPlayerJoinMap(newMap, p);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Mappack mappack = this.getMap(event.world).getMappack();
        if(mappack != null && mappack.getMappackMetadata().isPreventingBlockBreak()){
            if(!NailedAPI.getPlayerRegistry().getPlayer(event.getPlayer()).isEditModeEnabled()){
                event.setCanceled(true);
            }
            /*boolean inSecureZone = false;
            for( IZone zone : mappack.getMappackMetadata().getMapZones()){
                inSecureZone = (inSecureZone || (zone.isInZone(event.x, event.y, event.z) && zone.isSecure()));
            }
            if(!inSecureZone){

            } else if (!NailedAPI.getPlayerRegistry().getPlayer(event.getPlayer()).isSuperEditModeEnabled()){
                event.setCanceled(true);
            }*/
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        Map map = this.getMap(event.world);
        if(event.entity instanceof EntityPlayer && map.getMappack() != null){
            Mappack mappack = map.getMappack();
            EntityPlayer player = (EntityPlayer) event.entity;
            ChunkCoordinates worldSpawn = event.world.getSpawnPoint();
            Location spawn = mappack.getMappackMetadata().getSpawnPoint();
            if(Math.floor(player.posX) == worldSpawn.posX && Math.floor(player.posZ) == worldSpawn.posZ){
                event.entity.setLocationAndAngles(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
                player.setGameType(mappack.getMappackMetadata().getGamemode());
            }
        }
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        Map map = this.getMap(event.entity.worldObj);
        if(event.entity instanceof EntityPlayer && event.source instanceof EntityDamageSource){
            EntityDamageSource source = (EntityDamageSource) event.source;
            if(source.getEntity() instanceof EntityPlayer){
                Player src = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) source.getEntity());
                Player dest = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
                if(map instanceof NailedMap){
                    NailedMap m = (NailedMap) map;
                    m.getMachine().queueEvent("player_hit_player", src, dest, event.ammount);
                }
                if(map.getMappack() != null){
                    if(!map.getMappack().getMappackMetadata().isPvpEnabled()){
                        if(event.source instanceof PvpIgnoringDamageSource){
                            if(((PvpIgnoringDamageSource) event.source).disableWhenPvpDisabled()){
                                event.setCanceled(true);
                            }
                        }else{
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if(event.source.getEntity() instanceof EntityPlayer && event.entityLiving instanceof EntityPlayer){
            Mappack mappack = this.getMap(event.entity.worldObj).getMappack();
            if(mappack != null && !mappack.getMappackMetadata().isPvpEnabled()){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDie(LivingDeathEvent event) {
        if(!(event.entity instanceof EntityPlayer)){
            return;
        }
        Player player = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
        Map map = player.getCurrentMap();
        if(map instanceof NailedMap){
            NailedMap m = (NailedMap) map;
            if(event.source instanceof EntityDamageSource && event.source.getEntity() instanceof EntityPlayer){
                Player source = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.source.getEntity());
                m.getMachine().queueEvent("player_kill_player", source, player, event.source.damageType);
            }else{
                m.getMachine().queueEvent("player_die", player, event.source.damageType);
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        for(Map map : this.maps){
            map.getSignCommandHandler().onChunkLoad(event);
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        for(Map map : this.maps){
            map.getSignCommandHandler().onChunkUnload(event);
        }
    }

    @SubscribeEvent
    public void onWatch(ChunkWatchEvent.Watch event) {
        for(Map map : this.maps){
            map.getSignCommandHandler().onWatch(event);
        }
    }

    @SubscribeEvent
    public void onUnwatch(ChunkWatchEvent.UnWatch event) {
        for(Map map : this.maps){
            map.getSignCommandHandler().onUnwatch(event);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        //TODO: replace this
        //for(Map map : this.maps){
        for(int i = 0; i < this.maps.size(); i++){
            this.maps.get(i).getSignCommandHandler().onInteract(event);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        for(int i = 0; i < this.maps.size(); i++){
            this.maps.get(i).onTick(event);
        }
    }

    @SubscribeEvent
    public void onPreSpawn(LivingSpawnEvent.CheckSpawn event) {
        Map map = this.getMap(event.world);
        if(map.getMappack() != null){
            MappackMetadata meta = map.getMappack().getMappackMetadata();
            if(!meta.getSpawnRules().maySpawn(event.entity)){
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @Override
    public void removeMap(@Nonnull Map map) {
        Preconditions.checkNotNull(map, "map");
        if(map instanceof NailedMap){
            ((NailedMap) map).getMachine().destroy();
        }
        DimensionManager.unloadWorld(map.getID());
        DimensionManager.unregisterDimension(map.getID());
        this.maps.remove(map);
        this.releaseDimensionId(map.getID());
        MinecraftForge.EVENT_BUS.post(new MapRemovedEvent(map));
        NailedLog.info("Unloaded map {}", map.getSaveFileName());
    }

    public void checkShouldStart(@Nonnull Map map) {
        Preconditions.checkNotNull(map, "map");
        if(map.getMappack() == null){
            return;
        }
        String startWhen = map.getMappack().getMappackMetadata().getStartWhen();
        if("false".equals(startWhen)){
            return;
        }
        if(startWhen.startsWith("equals(")){
            String[] s = startWhen.substring(7, startWhen.length() - 1).split(",");
            if("joinedPlayers".equals(s[0])){
                int players = Integer.parseInt(s[1]);
                if(map.getAmountOfPlayers() >= players){
                    map.getGameManager().startGame();
                }else{
                    if(map.getGameManager().isWatchUnready()){
                        map.getGameManager().stopGame();
                    }
                }
            }
        }
    }

    @Nonnull
    public File getMapsFolder() {
        return mapsFolder;
    }

    @Nonnull
    public Map getLobby() {
        if(this.lobby == null){
            if(IpcManager.instance().isConnected()){
                this.lobby = new NailedMap(IpcMappackRegistry.getLobbyMappack(), 0);
            }else{
                this.lobby = new NailedMap(NailedAPI.getMappackLoader().getMappack("lobby"), 0);
            }
            if(this.lobby.getMappack() != null){
                this.lobby.getMappack().prepareWorld(this.lobby.getSaveFolder(), null);
            }
        }
        return lobby;
    }

    @Nonnull
    public List<Map> getMaps() {
        return maps;
    }

    @Nonnull
    public Random getRandomSpawnpointSelector() {
        return randomSpawnpointSelector;
    }

    public int reserveDimensionId() {
        int next = 0;
        while(true){
            next = dimensionIds.nextClearBit(next);
            dimensionIds.set(next);
            if(next == -1 || next == 0 || next == 1) continue;
            if(!dimensions.containsKey(next)){
                return next;
            }
        }
    }

    public void releaseDimensionId(int id) {
        dimensionIds.clear(id);
    }
}
