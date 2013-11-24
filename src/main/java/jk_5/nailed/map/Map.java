package jk_5.nailed.map;

import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.NailedLog;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.map.teleport.TeleportOptions;
import jk_5.nailed.network.NailedSPH;
import jk_5.nailed.server.ProxyCommon;
import lombok.Getter;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * No description given
 *
 * @author jk-5
 */
public class Map {

    @Getter private int ID = DimensionManager.getNextFreeDimId();
    @Getter private final Mappack mappack;
    @Getter private World world;
    @Getter private boolean isLoaded = false;

    public Map(Mappack mappack, int id){
        this.ID = id;
        this.mappack = mappack;
        MapLoader.instance().addMap(this);
    }

    void initMapServer(){
        if(this.isLoaded) return;
        NailedLog.info("Initializing %d", this.getID());

        DimensionManager.registerDimension(this.getID(), ProxyCommon.providerID);
        DimensionManager.initDimension(this.getID());

        this.setWorld(DimensionManager.getWorld(this.getID()));

        NailedSPH.broadcastRegisterDimension(this.getID());
    }

    public void sendMessageToAllPlayers(String message){
        PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat(message));
    }

    public void setWorld(World world){
        if(world == null) throw new NullPointerException("World should not be null!");
        this.world = world;
        if(world.provider != null) this.ID = world.provider.dimensionId;
        this.isLoaded = true;
        NailedLog.info("Registered world " + world);
    }

    public String getSaveFileName(){
        return PotentialMap.getSaveFileName(this);
    }

    public TeleportOptions getSpawnTeleport(){
        return new TeleportOptions(this, new ChunkCoordinates(this.mappack.getMappackMetadata().getSpawnPoint()), 0);
    }
}
