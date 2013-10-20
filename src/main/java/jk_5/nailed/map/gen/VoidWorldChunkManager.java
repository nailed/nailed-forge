package jk_5.nailed.map.gen;

import cpw.mods.fml.common.FMLLog;
import jk_5.nailed.NailedLog;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.List;
import java.util.Random;

public class VoidWorldChunkManager extends WorldChunkManager
{
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
            ChunkCoordinates spawn = new ChunkCoordinates(0, 64, 0);//MapLoader.getMapFromWorld(this.world).getSpawnPoint();
            NailedLog.info("Building spawn platform at: %d, %d, %d", spawn.posX, spawn.posY, spawn.posZ);
            world.setBlock(spawn.posX, spawn.posY, spawn.posZ, Block.bedrock.blockID);
        }
        return ret;
    }
}