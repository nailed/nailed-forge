package jk_5.nailed.map;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.gen.VoidWorldChunkManager;
import jk_5.nailed.server.ProxyCommon;
import lombok.Getter;
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

    @Getter private final int UID = nextId.getAndIncrement();
    @Getter private Mappack mappack;
    @Getter private World world;
    private WorldChunkManager chunkManager;

    public Map(Mappack mappack){
        this.world = world;
        //this.chunkManager = new VoidWorldChunkManager(world);

        NailedLog.info("Initializing %d", this.getUID());

        DimensionManager.registerDimension(this.getUID(), ProxyCommon.providerID);
        DimensionManager.initDimension(this.getUID());

        this.setWorld(DimensionManager.getWorld(this.getUID()));
    }

    public void setWorld(World world){
        this.world = world;
        NailedLog.info("Registered world " + world);
    }
}
