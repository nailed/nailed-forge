package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.NailedTileEntity;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlock extends Block {

    @Getter private final String registeredName;

    public NailedBlock(String name, Material material){
        super(material);
        this.registeredName = name;
        this.func_149663_c("nailed." + name);
    }

    @Override
    public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ){
        TileEntity tile = world.func_147438_o(x, y, z);
        if(tile != null && tile instanceof NailedTileEntity){
            return ((NailedTileEntity) tile).onBlockActivated(entity, side, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public void func_149749_a(World world, int x, int y, int z, Block oldBlock, int oldMeta){
        super.func_149749_a(world, x, y, z, oldBlock, oldMeta);
        world.func_147475_p(x, y, z);
    }

    @Override
    public boolean func_149696_a(World world, int x, int y, int z, int eventID, int data){
        super.func_149696_a(world, x, y, z, eventID, data);
        TileEntity tileentity = world.func_147438_o(x, y, z);
        return tileentity != null && tileentity.func_145842_c(eventID, data);
    }
}
