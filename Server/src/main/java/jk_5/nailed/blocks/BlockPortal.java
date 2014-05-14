package jk_5.nailed.blocks;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;
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
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean renderAsNormalBlock(){
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
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block block){
        if(par1World.isRemote) return;
        validate(par1World, new ChunkCoordinates(par2, par3, par4));
    }

    @Override
    public int quantityDropped(Random rand){
        return 0;
    }

    @Override
    public int getRenderBlockPass(){
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
        if(world.isRemote) return;
        TileEntity tileentity = BlockPortalController.getTileEntity(world, x, y, z);
        if((tileentity == null) || (!(tileentity instanceof TileEntityPortalController))){
            world.setBlockToAir(x, y, z);
            return;
        }
        TileEntityPortalController container = (TileEntityPortalController) tileentity;
        if(container.getDestination() == null){
            world.setBlockToAir(x, y, z);
            return;
        }
        TeleportOptions options = container.getDestination().reMake();
        options.setMaintainMomentum(true);
        options.setSound("nailed:teleport.portal");
        NailedAPI.getTeleporter().teleportEntity(entity, options);
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random rand){
        if(world.isRemote) return;
        this.onNeighborBlockChange(world, i, j, k, Blocks.air);
    }

    public static void validate(World world, ChunkCoordinates start){
        if(world.isRemote) return;
        List<ChunkCoordinates> blocks = Lists.newLinkedList();
        blocks.add(start);
        while(blocks.size() > 0){
            ChunkCoordinates coords = blocks.remove(0);
            if(world.getBlock(coords.posX, coords.posY, coords.posZ) == NailedBlocks.portal){
                validate(world, coords.posX, coords.posY, coords.posZ, blocks);
            }
        }
    }

    private static void validate(World world, int i, int j, int k, Collection<ChunkCoordinates> blocks){
        if(!isValidPortal(world, i, j, k)){
            world.setBlockToAir(i, j, k);
            BlockPortalController.addSurrounding(blocks, i, j, k);
        }
    }

    public static boolean isValidPortal(World world, int i, int j, int k){
        if(world.isRemote) return true;
        if(!checkPortalTension(world, i, j, k)) return false;
        return BlockPortalController.getTileEntity(world, i, j, k) != null;
    }

    public static boolean checkPortalTension(World world, int i, int j, int k){
        if(world.isRemote) return true;
        int score = 0;
        if((BlockPortalController.isValidLinkPortalBlock(world.getBlock(i + 1, j, k)) > 0) && (BlockPortalController.isValidLinkPortalBlock(world.getBlock(i - 1, j, k)) > 0)){
            score++;
        }
        if((BlockPortalController.isValidLinkPortalBlock(world.getBlock(i, j + 1, k)) > 0) && (BlockPortalController.isValidLinkPortalBlock(world.getBlock(i, j - 1, k)) > 0)){
            score++;
        }
        if((BlockPortalController.isValidLinkPortalBlock(world.getBlock(i, j, k + 1)) > 0) && (BlockPortalController.isValidLinkPortalBlock(world.getBlock(i, j, k - 1)) > 0)){
            score++;
        }
        return score > 1;
    }

    @Override
    public Block getReplacementBlock(){
        return Blocks.portal;
    }

    @Override
    public int getReplacementMetadata(){
        return 0;
    }
}
