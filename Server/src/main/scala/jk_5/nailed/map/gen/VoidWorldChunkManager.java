package jk_5.nailed.map.gen;

import java.util.*;

import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;

public class VoidWorldChunkManager extends WorldChunkManager {

    private World world;

    public VoidWorldChunkManager(World world) {
        super(world);
        this.world = world;
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int range, List biomes, Random rand) {
        ChunkPosition ret = super.findBiomePosition(x, z, range, biomes, rand);
        if(x == 0 && z == 0 && !world.getWorldInfo().isInitialized()){
            if(ret == null){
                ret = new ChunkPosition(0, 0, 0);
            }
            Map map = NailedAPI.getMapLoader().getMap(this.world);
            ChunkCoordinates spawn;
            if(map.getMappack() != null){
                spawn = map.getMappack().getMappackMetadata().getSpawnPoint().toChunkCoordinates();
            }else{
                spawn = new ChunkCoordinates(0, 65, 0);
            }
            NailedLog.info("Building spawn platform at {}, {}, {}", spawn.posX, spawn.posY - 1, spawn.posZ);
            if(world.isAirBlock(spawn.posX, spawn.posY - 1, spawn.posZ)){
                world.setBlock(spawn.posX, spawn.posY - 1, spawn.posZ, Blocks.bedrock);
            }
        }
        return ret;
    }
}
