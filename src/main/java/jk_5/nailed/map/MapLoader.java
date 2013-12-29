package jk_5.nailed.map;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.IMappackRegistrar;
import jk_5.nailed.event.PlayerChangedDimensionEvent;
import jk_5.nailed.map.event.MapCreatedEvent;
import jk_5.nailed.map.event.MapRemovedEvent;
import jk_5.nailed.map.mappack.DirectoryMappack;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.map.mappack.ZipMappack;
import jk_5.nailed.players.PlayerRegistry;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapLoader implements IMappackRegistrar {

    private static final MapLoader INSTANCE = new MapLoader();
    @SuppressWarnings("unused") @Getter private static final File mappackFolder = new File("mappacks");
    @SuppressWarnings("unused") @Getter private static final File mapsFolder = new File("maps");

    @Getter private final List<Map> maps = Lists.newArrayList();
    @Getter private final List<Mappack> mappacks = Lists.newArrayList();

    @Getter private Map lobby;

    public static MapLoader instance(){
        return INSTANCE;
    }

    public MapLoader(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void loadMappacks(){
        NailedLog.info("Loading mappacks...");
        this.mappacks.clear();
        if(!mappackFolder.exists()) mappackFolder.mkdirs();
        File[] list = mappackFolder.listFiles();
        if(list == null) return;
        for(File file : list){
            try{
                if(file.isFile() && (file.getName().endsWith(".zip") || file.getName().endsWith(".mappack"))){
                    this.mappacks.add(ZipMappack.create(file));
                    NailedLog.info("Successfully loaded mappack " + file.getName());
                }else if(file.isDirectory()){
                    this.mappacks.add(DirectoryMappack.create(file));
                    NailedLog.info("Successfully loaded mappack " + file.getName());
                }
            }catch (DiscardedMappackInitializationException e){
                //Discard!
                NailedLog.warning("An error was thrown while loading mappack " + file.getName() + ", skipping it!");
            }catch (MappackInitializationException e){
                NailedLog.severe(e, "Error while loading mappack " + file.getName() + ", skipping it!");
            }
        }
        NailedLog.info("Successfully loaded %d mappacks!", this.mappacks.size());
    }

    public Mappack getMappack(String mappackID){
        for(Mappack pack : this.mappacks){
            if(pack.getMappackID().equals(mappackID)){
                return pack;
            }
        }
        return null;
    }

    public void addMap(Map map){
        if(map.getID() == 0) this.lobby = map;
        this.maps.add(map);
        NailedLog.info("Registered " + map.getSaveFileName());
    }

    public Map newMapServerFor(Mappack pack){
        PotentialMap potentialMap = new PotentialMap(pack);
        pack.prepareWorld(potentialMap.getSaveFolder());
        Map map = pack.createMap(potentialMap);
        map.initMapServer();
        MinecraftForge.EVENT_BUS.post(new MapCreatedEvent(map));
        return map;
    }

    public Map getMapFromName(String name){
        for(Map map : this.maps){
            if(map.getSaveFileName().equalsIgnoreCase(name)){
                return map;
            }
        }
        return null;
    }

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

    public Map getMap(World world){
        if(world == null) return null;
        return this.getMap(world.provider.dimensionId);
    }

    @ForgeSubscribe(priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onWorldLoad(WorldEvent.Load event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Map map = this.getMap(event.world);
        if(map != null) map.setWorld(event.world);
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onChangeDimension(PlayerChangedDimensionEvent event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        event.oldMap.onPlayerLeft(event.player);
        event.newMap.onPlayerJoined(event.player);
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockEvent.BreakEvent event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Mappack mappack = this.getMap(event.world).getMappack();
        if(mappack != null && mappack.getMappackMetadata().isPreventingBlockBreak()){
            event.setCanceled(true);
        }
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onEntitySpawn(EntityJoinWorldEvent event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Map map = this.getMap(event.world);
        if(event.entity instanceof EntityPlayer && map.getMappack() != null){
            Mappack mappack = map.getMappack();
            EntityPlayer player = (EntityPlayer) event.entity;
            ChunkCoordinates spawn = event.world.getSpawnPoint();
            if(player.posX - 0.5 == spawn.posX && player.posZ - 0.5 == spawn.posZ){
                event.entity.setLocationAndAngles(player.posX, player.posY, player.posZ, mappack.getMappackMetadata().getSpawnYaw(), mappack.getMappackMetadata().getSpawnPitch());
                player.setGameType(mappack.getMappackMetadata().getGamemode());
                map.onPlayerJoined(PlayerRegistry.instance().getOrCreatePlayer(player.username));
            }
        }
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onDamage(LivingHurtEvent event){
        if(event.entity instanceof EntityPlayer && event.source instanceof EntityDamageSource){
            EntityDamageSource source = (EntityDamageSource) event.source;
            if(source.getEntity() instanceof EntityPlayer){
                Map map = this.getMap(event.entity.worldObj);
                if(map.getMappack() != null){
                    if(!map.getMappack().getMappackMetadata().isPvpEnabled()){
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public void registerMappack(Mappack mappack) {
        this.mappacks.add(mappack);
    }

    public void removeMap(Map map){
        DimensionManager.unloadWorld(map.getID());
        this.maps.remove(map);
        MinecraftForge.EVENT_BUS.post(new MapRemovedEvent(map));
        NailedLog.info("Unloaded map " + map.getSaveFileName());
    }
}
