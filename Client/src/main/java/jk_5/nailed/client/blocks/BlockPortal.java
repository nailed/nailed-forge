package jk_5.nailed.client.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.blocks.tileentity.TileEntityPortalController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortal extends NailedBlock {

    public BlockPortal(){
        super("portal", Material.portal);
        this.setTickRandomly(true);
        this.setLightLevel(1);
        this.setBlockUnbreakable();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register){
        this.blockIcon = register.registerIcon("nailed:portal");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z){
        float xmin = 0.25F;
        float xmax = 0.75F;
        float ymin = 0.25F;
        float ymax = 0.75F;
        float zmin = 0.25F;
        float zmax = 0.75F;
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x - 1, y, z)) > 0){
            xmin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x + 1, y, z)) > 0){
            xmax = 1.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x, y - 1, z)) > 0){
            ymin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x, y + 1, z)) > 0){
            ymax = 1.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x, y, z - 1)) > 0){
            zmin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.getBlock(x, y, z + 1)) > 0){
            zmax = 1.0F;
        }
        this.setBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    @Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public int getRenderType(){
        return 0;
    }

    @Override
    public int getBlockColor(){
        return 0x3333FF;
    }

    @Override
    public int getRenderColor(int i){
        return 0x3333FF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side){
        Block block = world.getBlock(x, y, z);
        return block != this && super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z){
        World world = Minecraft.getMinecraft().theWorld;
        TileEntity entity = BlockPortalController.getTileEntity(world, x, y, z);
        if((entity != null) && ((entity instanceof TileEntityPortalController))){
            TileEntityPortalController controller = (TileEntityPortalController) entity;
            return controller.getColor();
        }
        return this.getBlockColor();
    }

    @Override
    public int quantityDropped(Random rand){
        return 0;
    }

    @Override
    public int getRenderBlockPass(){
        return 1;
    }
}
