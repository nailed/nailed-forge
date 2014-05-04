package jk_5.nailed.map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.events.MapCreatedEvent;
import jk_5.nailed.api.events.MapRemovedEvent;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.map.script.api.MapApi;
import jk_5.nailed.players.TeamUndefined;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

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

    @Nonnull
    public static NailedMapLoader instance(){
        if(NailedAPI.getMapLoader() == null){
            NailedAPI.setMapLoader(new NailedMapLoader());
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public NailedMapLoader(){
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

    @Override
    public void registerMap(@Nonnull Map map){
        Preconditions.checkNotNull(map, "map");
        if(map.getID() == 0) this.lobby = map;
        this.maps.add(map);
        NailedLog.info("Registered {}", map.getSaveFileName());
    }

    @Override
    public void createMapServer(@Nonnull final Mappack mappack, @Nullable final Callback<Map> callback){
        Preconditions.checkNotNull(mappack, "mappack");
        final PotentialMap potentialMap = new PotentialMap(mappack);
        NailedLog.info("Scheduling the load of {}", potentialMap.getSaveFileName());
        NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run(){
                NailedLog.info("Preparing {}", potentialMap.getSaveFileName());
                mappack.prepareWorld(potentialMap.getSaveFolder(), new Callback<Void>() {
                    @Override
                    public void callback(Void obj){
                        final Map map = mappack.createMap(potentialMap);
                        NailedAPI.getScheduler().runTask(new NailedRunnable() {
                            @Override
                            public void run(){
                                NailedLog.info("Loading {}", potentialMap.getSaveFileName());
                                map.initMapServer();
                                MinecraftForge.EVENT_BUS.post(new MapCreatedEvent(map));
                                if(callback != null) callback.callback(map);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    @Nullable
    public Map getMap(@Nonnull String name){
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
    public Map getMap(int id){
        for(Map map : this.maps){
            if(map.getID() == id){
                return map;
            }
        }
        if(DimensionManager.isDimensionRegistered(id)){
            return new WrappedMap(id);
        }else{
            return null;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public Map getMap(@Nonnull World world){
        Preconditions.checkNotNull(world, "world");
        return this.getMap(world.provider.dimensionId);
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        Map map = this.getMap(event.player.worldObj);
        map.onPlayerJoined(NailedAPI.getPlayerRegistry().getPlayer(event.player));

        if(event.player.worldObj.provider instanceof NailedWorldProvider){
            ((NailedWorldProvider) event.player.worldObj.provider).sendMapData(event.player);
        }
    }

    @SubscribeEvent
    public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        Map map = this.getMap(event.player.worldObj);
        map.onPlayerLeft(NailedAPI.getPlayerRegistry().getPlayer(event.player));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldEvent.Load event){
        this.getMap(event.world).setWorld(event.world);
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        Player p = NailedAPI.getPlayerRegistry().getPlayer(event.player);
        Map oldMap = p.getCurrentMap();
        Map newMap = this.getMap(event.player.worldObj);
        oldMap.onPlayerLeft(p);
        newMap.onPlayerJoined(p);
        for(Map map : this.maps){
            map.getSignCommandHandler().onPlayerLeftMap(oldMap, p);
            map.getSignCommandHandler().onPlayerJoinMap(newMap, p);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event){
        Mappack mappack = this.getMap(event.world).getMappack();
        if(mappack != null && mappack.getMappackMetadata().isPreventingBlockBreak()){
            //event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event){
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
    public void onDamage(LivingHurtEvent event){
        Map map = this.getMap(event.entity.worldObj);
        if(event.entity instanceof EntityPlayer && event.source instanceof EntityDamageSource){
            EntityDamageSource source = (EntityDamageSource) event.source;
            if(source.getEntity() instanceof EntityPlayer){
                Player src = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) source.getEntity());
                Player dest = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
                if(map instanceof NailedMap){
                    NailedMap m = (NailedMap) map;
                    m.getMachine().queueEvent("player_hit_player", MapApi.wrapPlayer(m.getMachine().getApiEnvironment(), src), MapApi.wrapPlayer(m.getMachine().getApiEnvironment(), dest), event.ammount);
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
    public void onAttack(LivingAttackEvent event){
        if(event.source.getEntity() instanceof EntityPlayer && event.entityLiving instanceof EntityPlayer){
            Mappack mappack = this.getMap(event.entity.worldObj).getMappack();
            if(mappack != null && !mappack.getMappackMetadata().isPvpEnabled()){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDie(LivingDeathEvent event){
        if(!(event.entity instanceof EntityPlayer)) return;
        Player player = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
        Map map = player.getCurrentMap();
        if(map instanceof NailedMap){
            NailedMap m = (NailedMap) map;
            if(event.source instanceof EntityDamageSource && event.source.getEntity() instanceof EntityPlayer){
                Player source = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.source.getEntity());
                m.getMachine().queueEvent("player_kill_player", MapApi.wrapPlayer(m.getMachine().getApiEnvironment(), source), MapApi.wrapPlayer(m.getMachine().getApiEnvironment(), player), event.source.damageType);
            }else{
                m.getMachine().queueEvent("player_die", MapApi.wrapPlayer(m.getMachine().getApiEnvironment(), player), event.source.damageType);
            }
        }
        if(!player.getCurrentMap().getGameManager().isGameRunning()) return;
        if(player.getSpawnpoint() != null){
            player.getEntity().setSpawnChunk(player.getSpawnpoint(), true);
        }else if(player.getTeam() instanceof TeamUndefined){
            Mappack mappack = map.getMappack();
            if(mappack != null && mappack.getMappackMetadata().isChoosingRandomSpawnpointAtRespawn()){
                List<Location> spawnpoints = mappack.getMappackMetadata().getRandomSpawnpoints();
                Location chosen = spawnpoints.get(this.randomSpawnpointSelector.nextInt(spawnpoints.size()));
                player.getEntity().setSpawnChunk(chosen.toChunkCoordinates(), true); //FIXME
            }
        }else{
            if(player.getTeam().shouldOverrideDefaultSpawnpoint()){
                Location coords = player.getTeam().getSpawnpoint();
                player.getEntity().setSpawnChunk(coords.toChunkCoordinates(), true); //FIXME
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event){
        for(Map map : this.maps){
            map.getSignCommandHandler().onChunkLoad(event);
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event){
        for(Map map : this.maps){
            map.getSignCommandHandler().onChunkUnload(event);
        }
    }

    @SubscribeEvent
    public void onWatch(ChunkWatchEvent.Watch event){
        for(Map map : this.maps){
            map.getSignCommandHandler().onWatch(event);
        }
    }

    @SubscribeEvent
    public void onUnwatch(ChunkWatchEvent.UnWatch event){
        for(Map map : this.maps){
            map.getSignCommandHandler().onUnwatch(event);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event){
        //TODO: replace this
        //for(Map map : this.maps){
        for(int i = 0; i < this.maps.size(); i++){
            this.maps.get(i).getSignCommandHandler().onInteract(event);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        for(int i = 0; i < this.maps.size(); i++){
            this.maps.get(i).onTick(event);
        }
    }

    @SubscribeEvent
    public void onPreSpawn(LivingSpawnEvent.CheckSpawn event){
        Map map = this.getMap(event.world);
        if(map.getMappack() != null){
            MappackMetadata meta = map.getMappack().getMappackMetadata();
            if(!meta.getSpawnRules().maySpawn(event.entity)){
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @Override
    public void removeMap(@Nonnull Map map){
        Preconditions.checkNotNull(map, "map");
        if(map instanceof NailedMap){
            ((NailedMap) map).getMachine().destroy();
        }
        DimensionManager.unloadWorld(map.getID());
        this.maps.remove(map);
        this.releaseDimensionId(map.getID());
        MinecraftForge.EVENT_BUS.post(new MapRemovedEvent(map));
        NailedLog.info("Unloaded map {}", map.getSaveFileName());
    }

    public void checkShouldStart(@Nonnull Map map){
        Preconditions.checkNotNull(map, "map");
        if(map.getMappack() == null) return;
        String startWhen = map.getMappack().getMappackMetadata().getStartWhen();
        if(startWhen.equals("false")) return;
        if(startWhen.startsWith("equals(")){
            String s[] = startWhen.substring(7, startWhen.length() - 1).split(",");
            if(s[0].equals("joinedPlayers")){
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
    public File getMapsFolder(){
        return mapsFolder;
    }

    @Nonnull
    public Map getLobby(){
        return lobby;
    }

    @Nonnull
    public List<Map> getMaps(){
        return maps;
    }

    @Nonnull
    public Random getRandomSpawnpointSelector(){
        return randomSpawnpointSelector;
    }

    public int reserveDimensionId(){
        int next = 0;
        while (true){
            next = dimensionIds.nextClearBit(next);
            dimensionIds.set(next);
            if(!dimensions.containsKey(next)){
                return next;
            }
        }
    }

    public void releaseDimensionId(int id){
        dimensionIds.clear(id);
    }
}
