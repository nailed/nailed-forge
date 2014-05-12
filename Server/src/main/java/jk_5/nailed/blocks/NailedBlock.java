package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.NailedTileEntity;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
        this.setBlockName("nailed." + name);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ){
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile != null && tile instanceof NailedTileEntity){
            return ((NailedTileEntity) tile).onBlockActivated(entity, side, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int data){
        super.onBlockEventReceived(world, x, y, z, eventID, data);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(eventID, data);
    }

    public static Block getReplacementBlock(){
        return (Blocks.air);
    }

    public static int getReplacementMetadata(){
        return 0;
    }
}
