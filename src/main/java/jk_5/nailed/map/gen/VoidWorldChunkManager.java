package jk_5.nailed.map.gen;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.List;
import java.util.Random;

public class VoidWorldChunkManager extends WorldChunkManager {

    private World world;
    
    public VoidWorldChunkManager(World world){
        super(world);
        this.world = world;
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int range, List biomes, Random rand){
        ChunkPosition ret = super.findBiomePosition(x, z, range, biomes, rand);

        if (x == 0 && z == 0 && !world.getWorldInfo().isInitialized()){
            if (ret == null){
                ret = new ChunkPosition(0, 0, 0);
            }
            Map map = MapLoader.instance().getMap(this.world);
            ChunkCoordinates spawn;
            if(map != null && map.getMappack() != null){
                spawn = map.getMappack().getMappackMetadata().getSpawnPoint();
            }else{
                spawn = new ChunkCoordinates(0, 65, 0);
            }
            NailedLog.info("Building spawn platform at: %d, %d, %d", spawn.posX, spawn.posY - 1, spawn.posZ);
            world.setBlock(spawn.posX, spawn.posY - 1, spawn.posZ, Block.bedrock.blockID);
        }
        return ret;
    }
}
