package jk_5.nailed.map.gen;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;

public class VoidChunkProvider extends ChunkProviderFlat {

    private World world;

    public VoidChunkProvider(World world){
        super(world, world.getSeed(), false, null);
        this.world = world;
    }

    @Override
    public Chunk loadChunk(int par1, int par2){
        return this.provideChunk(par1, par2);
    }

    @Override
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3){

    }

    @Override
    public Chunk provideChunk(int x, int y){
        Chunk ret = new Chunk(this.world, new Block[32768], x, y);
        this.world.getWorldChunkManager().loadBlockGeneratorData(null, x * 16, y * 16, 16, 16);
        ret.generateSkylightMap();
        return ret;
    }
}
