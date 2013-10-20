package jk_5.nailed.map.gen;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * No description given
 *
 * @author jk-5
 */
public class VoidWorldType extends WorldType {

    public VoidWorldType(){
        super(0, "default");
    }

    @Override
    public WorldChunkManager getChunkManager(World world){
        return new VoidWorldChunkManager(world);
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions){
        return new VoidChunkProvider(world);
    }

    @Override
    public int getSpawnFuzz(){
        return 1;
    }
}
