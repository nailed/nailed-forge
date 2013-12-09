package jk_5.nailed.blocks;

import jk_5.nailed.NailedModContainer;
import jk_5.nailed.blocks.tileentity.NailedTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlock extends Block {

    public static AtomicInteger nextId = new AtomicInteger(3682);

    public NailedBlock(String name, Material material){
        super(NailedModContainer.getConfig().getTag("blocks").useBraces().getTag(name).useBraces().getTag("id").getIntValue(nextId.getAndIncrement()), material);
        this.setUnlocalizedName("nailed." + name);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn(){
        return NailedBlocks.creativeTab;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ){
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if(tile != null && tile instanceof NailedTileEntity){
            return ((NailedTileEntity) tile).onBlockActivated(entity, side, hitX, hitY, hitZ);
        }
        return false;
    }
}
