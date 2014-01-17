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
        super("portal", Material.field_151570_A); //Material.portal
        this.func_149675_a(true);
        this.func_149715_a(1);
        this.func_149722_s();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister register){
        //this.blockIcon
        this.field_149761_L = register.registerIcon("nailed:portal");
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int par2, int par3, int i){
        return null;
    }

    @Override
    public void func_149719_a(IBlockAccess blockAccess, int x, int y, int z){
        float xmin = 0.25F;
        float xmax = 0.75F;
        float ymin = 0.25F;
        float ymax = 0.75F;
        float zmin = 0.25F;
        float zmax = 0.75F;
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x - 1, y, z)) > 0){
            xmin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x + 1, y, z)) > 0){
            xmax = 1.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x, y - 1, z)) > 0){
            ymin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x, y + 1, z)) > 0){
            ymax = 1.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x, y, z - 1)) > 0){
            zmin = 0.0F;
        }
        if(BlockPortalController.isValidLinkPortalBlock(blockAccess.func_147439_a(x, y, z + 1)) > 0){
            zmax = 1.0F;
        }
        this.func_149676_a(xmin, ymin, zmin, xmax, ymax, zmax);
    }

    @Override
    public boolean func_149662_c(){
        return false;
    }

    @Override
    public boolean func_149686_d(){
        return false;
    }

    @Override
    public int func_149645_b(){
        return 0;
    }

    @Override
    public int func_149635_D(){
        return 0x3333FF;
    }

    @Override
    public int func_149741_i(int i){
        return 0x3333FF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess world, int x, int y, int z, int side){
        Block block = world.func_147439_a(x, y, z);
        return block != this && super.func_149646_a(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int func_149720_d(IBlockAccess blockAccess, int x, int y, int z){
        World world = Minecraft.getMinecraft().theWorld;
        TileEntity entity = BlockPortalController.getTileEntity(world, x, y, z);
        if((entity != null) && ((entity instanceof TileEntityPortalController))){
            TileEntityPortalController controller = (TileEntityPortalController) entity;
            return controller.getColor();
        }
        return func_149635_D();
    }

    @Override
    public int func_149745_a(Random rand){
        return 0;
    }

    @Override
    public int func_149701_w(){
        return 1;
    }
}
