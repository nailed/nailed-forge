package jk_5.nailed.map;

import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.NailedLog;
import jk_5.nailed.map.gen.VoidWorldChunkManager;
import jk_5.nailed.network.packets.PacketRegisterDimension;
import jk_5.nailed.server.ProxyCommon;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.common.DimensionManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
public class Map {

    private static final AtomicInteger nextId = new AtomicInteger(10);

    @Getter private final int ID = DimensionManager.getNextFreeDimId();
    @Getter private Mappack mappack;
    @Getter private World world;
    private WorldChunkManager chunkManager;

    public Map(Mappack mappack){
        this.world = world;
        this.mappack = mappack;
        //this.chunkManager = new VoidWorldChunkManager(world);

        NailedLog.info("Initializing %d", this.getID());

        DimensionManager.registerDimension(this.getID(), ProxyCommon.providerID);
        DimensionManager.initDimension(this.getID());

        this.setWorld(DimensionManager.getWorld(this.getID()));

        PacketDispatcher.sendPacketToAllPlayers(new PacketRegisterDimension(this.getID()).getPacket());

        MapLoader.instance().addMap(this);
    }

    public void setWorld(World world){
        this.world = world;
        NailedLog.info("Registered world " + world);
    }
}
