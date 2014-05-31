package jk_5.nailed.client.blocks;

import java.util.*;

import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.util.*;

import jk_5.nailed.client.blocks.tileentity.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockInvisibleWall extends BlockMulti {

    public BlockInvisibleWall(){
        super("invisibleBlock", Material.glass); //Material.glass

        this.disableStats(); //disableStats
        this.setBlockUnbreakable(); //setBlockUnbreakable
        this.setLightOpacity(0); //setLightOpacity
        this.setHardness(6000000.0F); //setHardness
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z){
        int meta = world.getBlockMetadata(x, y, z);
        return meta == 2 ? 15 : 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        switch (stack.getItemDamage()){
            case 0: return "invisibleWall";
            case 1: return "invisibleBlock";
            case 2: return "invisibleLight";
            case 3: return "invisibleRedstone";
            case 4: return "sky";
            default: return "";
        }
    }

    @Override
    public IIcon getIcon(int side, int meta){
        return Blocks.tallgrass.getIcon(side, meta);
    }

    @Override
    public int quantityDropped(Random random){
        return 0;
    }

    @Override
    public int getRenderType(){
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        int meta = world.getBlockMetadata(x, y, z);
        if(meta == 1 || meta == 3){
            return null;
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public int getMobilityFlag(){
        return 2;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        //list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
    }

    @Override
    public boolean hasTileEntity(int metadata){
        return metadata == 4;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata){
        return metadata == 4 ? new TileEntitySky() : null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
        return new ItemStack(Item.getItemFromBlock(this), 1, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void registerBlockIcons(IIconRegister registry) {

    }
}
