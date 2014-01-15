package jk_5.nailed.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortalController extends NailedBlock implements ITileEntityProvider {

    private IIcon iconFace;

    public BlockPortalController(){
        super("portalController", Material.field_151592_s);
        this.func_149676_a(0, 0, 0, 1, 1, 0.375F);
    }

    @Override
    public TileEntity func_149915_a(World var1, int var2){
        return new TileEntityPortalController();
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
    public void func_149683_g(){
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
    }

    @Override
    public boolean func_149707_d(World world, int x, int y, int z, int side){
        if((side == 0) && (world.func_147439_a(x, y + 1, z) != NailedBlocks.portalCrystal)){
            return false;
        }else if((side == 1) && (world.func_147439_a(x, y - 1, z) != NailedBlocks.portalCrystal)){
            return false;
        }else if((side == 2) && (world.func_147439_a(x, y, z + 1) != NailedBlocks.portalCrystal)){
            return false;
        }else if((side == 3) && (world.func_147439_a(x, y, z - 1) != NailedBlocks.portalCrystal)){
            return false;
        }else if((side == 4) && (world.func_147439_a(x + 1, y, z) != NailedBlocks.portalCrystal)){
            return false;
        }else if((side == 5) && (world.func_147439_a(x - 1, y, z) != NailedBlocks.portalCrystal)){
            return false;
        }else{
            return func_149742_c(world, x, y, z);
        }
    }

    @Override
    public AxisAlignedBB func_149633_g(World par1World, int par2, int par3, int par4){
        int i = par1World.getBlockMetadata(par2, par3, par4);
        float f = 0.375F;

        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        if(i == 0){
            func_149676_a(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if(i == 1){
            func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if(i == 2){
            func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        if(i == 3){
            func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        }

        if(i == 4){
            func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if(i == 5){
            func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }
        AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(par2 + this.field_149759_B, par3 + this.field_149760_C, par4 + this.field_149754_D, par2 + this.field_149755_E, par3 + this.field_149756_F, par4 + this.field_149757_G);
        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
        return box;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity){
        func_149719_a(world, i, j, k);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
    }

    @Override
    public void func_149719_a(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
        int i = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        float f = 0.375F;

        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.375F);
        if(i == 0){
            func_149676_a(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if(i == 1){
            func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if(i == 2){
            func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        if(i == 3){
            func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        }

        if(i == 4){
            func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if(i == 5){
            func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int face, float par6, float par7, float par8, int metadata){
        if(face == 0)
            return face;
        if(face == 1)
            return face;
        if(face == 2)
            return face;
        if(face == 3)
            return face;
        if(face == 4)
            return face;
        if(face == 5){
            return face;
        }
        return 0;
    }

    @Override
    public void func_149726_b(World par1World, int par2, int par3, int par4){
        super.func_149726_b(par1World, par2, par3, par4);
        updateTileEntityOrientation(par1World, par2, par3, par4);
    }

    private void updateTileEntityOrientation(World world, int i, int j, int k){
        TileEntityPortalController controller = (TileEntityPortalController) world.func_147438_o(i, j, k);
        int metadata = world.getBlockMetadata(i, j, k);

        if(metadata == 1){
            controller.pitch = -90;
            controller.yaw = -90;
        }else if(metadata == 1){
            controller.pitch = 90;
            controller.yaw = -90;
        }else if(metadata == 2){
            controller.yaw = 270;
        }else if(metadata == 3){
            controller.yaw = 90;
        }else if(metadata == 4){
            controller.yaw = 0;
        }else if(metadata == 5){
            controller.yaw = 180;
        }

        controller.onInventoryChanged();
    }

    @Override
    public void func_149695_a(World par1World, int par2, int par3, int par4, Block block){
        ChunkCoordinates coord = getBase(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4));
        if(par1World.func_147439_a(coord.posX, coord.posY, coord.posZ) != NailedBlocks.portalCrystal){
            func_149697_b(par1World, par2, par3, par4, 0, 0);
            par1World.func_147468_f(par2, par3, par4);
        }

        super.func_149695_a(par1World, par2, par3, par4, block);
    }

    public static void fire(World world, int i, int j, int k){
        ChunkCoordinates coord = getBase(i, j, k, world.getBlockMetadata(i, j, k));
        onpulse(world, coord.posX, coord.posY, coord.posZ);
        pathto(world, i, j, k);
    }

    public static void shutdown(World world, int i, int j, int k){
        unpath(world, i, j, k);
    }

    private static ChunkCoordinates getBase(int i, int j, int k, int blockMetadata){
        if(blockMetadata == 0)
            return new ChunkCoordinates(i, j + 1, k);
        if(blockMetadata == 1)
            return new ChunkCoordinates(i, j - 1, k);
        if(blockMetadata == 2)
            return new ChunkCoordinates(i, j, k + 1);
        if(blockMetadata == 3)
            return new ChunkCoordinates(i, j, k - 1);
        if(blockMetadata == 4)
            return new ChunkCoordinates(i + 1, j, k);
        if(blockMetadata == 5){
            return new ChunkCoordinates(i - 1, j, k);
        }
        return new ChunkCoordinates(i, j, k);
    }

    private static void pathto(World world, int i, int j, int k){
        List<ChunkCoordinates> blocks = Lists.newLinkedList();
        List<ChunkCoordinates> portals = Lists.newLinkedList();
        List<ChunkCoordinates> repath = Lists.newLinkedList();
        List<ChunkCoordinates> redraw = Lists.newLinkedList();
        blocks.add(new ChunkCoordinates(i, j, k));
        while((portals.size() > 0) || (blocks.size() > 0)){
            while(blocks.size() > 0){
                ChunkCoordinates coords = blocks.remove(0);
                directPortal(world, coords.posX + 1, coords.posY, coords.posZ, 5, blocks, portals);
                directPortal(world, coords.posX, coords.posY + 1, coords.posZ, 1, blocks, portals);
                directPortal(world, coords.posX, coords.posY, coords.posZ + 1, 3, blocks, portals);
                directPortal(world, coords.posX - 1, coords.posY, coords.posZ, 6, blocks, portals);
                directPortal(world, coords.posX, coords.posY - 1, coords.posZ, 2, blocks, portals);
                directPortal(world, coords.posX, coords.posY, coords.posZ - 1, 4, blocks, portals);
                redraw.add(coords);
            }
            if(portals.size() > 0){
                ChunkCoordinates coords = portals.remove(0);
                directPortal(world, coords.posX + 1, coords.posY, coords.posZ, 5, blocks, portals);
                directPortal(world, coords.posX, coords.posY + 1, coords.posZ, 1, blocks, portals);
                directPortal(world, coords.posX, coords.posY, coords.posZ + 1, 3, blocks, portals);
                directPortal(world, coords.posX - 1, coords.posY, coords.posZ, 6, blocks, portals);
                directPortal(world, coords.posX, coords.posY - 1, coords.posZ, 2, blocks, portals);
                directPortal(world, coords.posX, coords.posY, coords.posZ - 1, 4, blocks, portals);
                if(world.func_147439_a(coords.posX, coords.posY, coords.posZ) == NailedBlocks.portal){
                    repath.add(coords);
                }
            }
        }
        while(repath.size() > 0){
            ChunkCoordinates coords = repath.remove(0);
            if(world.func_147439_a(coords.posX, coords.posY, coords.posZ) == NailedBlocks.portal){
                if(!BlockPortal.isValidPortal(world, coords.posX, coords.posY, coords.posZ)){
                    repathNeighbors(world, coords.posX, coords.posY, coords.posZ);
                    world.func_147465_d(coords.posX, coords.posY, coords.posZ, Blocks.air, 0, 0);
                    addSurrounding(repath, coords.posX, coords.posY, coords.posZ);
                }else{
                    redraw.add(coords);
                }
            }
        }
        for(ChunkCoordinates coords : redraw){
            if(world.blockExists(coords.posX, coords.posY, coords.posZ)){
                world.func_147471_g(coords.posX, coords.posY, coords.posZ);
                world.func_147459_d(coords.posX, coords.posY, coords.posZ, world.func_147439_a(coords.posX, coords.posY, coords.posZ));
            }
        }
    }

    private static void repathNeighbors(World world, int i, int j, int k){
        TileEntity tileentity = getTileEntity(world, i, j, k);
        List<ChunkCoordinates> blocks = Lists.newLinkedList();
        blocks.add(new ChunkCoordinates(i, j, k));
        world.setBlockMetadataWithNotify(i, j, k, 8, 2);
        while(blocks.size() > 0){
            ChunkCoordinates coords = blocks.remove(0);
            redirectPortal(world, tileentity, coords.posX + 1, coords.posY, coords.posZ, 5, blocks);
            redirectPortal(world, tileentity, coords.posX, coords.posY + 1, coords.posZ, 1, blocks);
            redirectPortal(world, tileentity, coords.posX, coords.posY, coords.posZ + 1, 3, blocks);
            redirectPortal(world, tileentity, coords.posX - 1, coords.posY, coords.posZ, 6, blocks);
            redirectPortal(world, tileentity, coords.posX, coords.posY - 1, coords.posZ, 2, blocks);
            redirectPortal(world, tileentity, coords.posX, coords.posY, coords.posZ - 1, 4, blocks);
        }
    }

    private static void redirectPortal(World world, TileEntity tileentity, int i, int j, int k, int meta, List<ChunkCoordinates> blocks){
        if(isValidLinkPortalBlock(world.func_147439_a(i, j, k)) == 0) return;
        if(world.getBlockMetadata(i, j, k) == meta){
            for(int m = 1; m < 7; m++)
                if(m != meta){
                    world.setBlockMetadataWithNotify(i, j, k, m, 2);
                    TileEntity local = getTileEntity(world, i, j, k);
                    if((local == tileentity) || ((local != null) && (tileentity == null))) return;
                }
            world.setBlockMetadataWithNotify(i, j, k, 0, 2);
        }
    }

    public static void unpath(World world, int i, int j, int k){
        List<ChunkCoordinates> blocks = Lists.newLinkedList();
        List<ChunkCoordinates> notify = Lists.newLinkedList();
        blocks.add(new ChunkCoordinates(i, j, k));
        while(blocks.size() > 0){
            ChunkCoordinates coords = blocks.remove(0);
            depolarize(world, coords.posX + 1, coords.posY, coords.posZ, blocks);
            depolarize(world, coords.posX, coords.posY + 1, coords.posZ, blocks);
            depolarize(world, coords.posX, coords.posY, coords.posZ + 1, blocks);
            depolarize(world, coords.posX - 1, coords.posY, coords.posZ, blocks);
            depolarize(world, coords.posX, coords.posY - 1, coords.posZ, blocks);
            depolarize(world, coords.posX, coords.posY, coords.posZ - 1, blocks);
            notify.add(coords);
        }
        for(ChunkCoordinates coords : notify)
            if(world.blockExists(coords.posX, coords.posY, coords.posZ)){
                world.func_147471_g(coords.posX, coords.posY, coords.posZ);
                world.func_147459_d(coords.posX, coords.posY, coords.posZ, world.func_147439_a(coords.posX, coords.posY, coords.posZ));
            }
    }

    private static void onpulse(World world, int i, int j, int k){
        List<ChunkCoordinates> set = Lists.newLinkedList();
        Stack<ChunkCoordinates> validate = new Stack<ChunkCoordinates>();
        addSurrounding(set, i, j, k);
        while(set.size() > 0){
            ChunkCoordinates coords = set.remove(0);
            expandPortal(world, coords.posX, coords.posY, coords.posZ, set, validate);
        }
        while(validate.size() > 0){
            ChunkCoordinates coords = validate.pop();
            i = coords.posX;
            j = coords.posY;
            k = coords.posZ;
            if(!BlockPortal.checkPortalTension(world, i, j, k))
                world.func_147465_d(i, j, k, Blocks.air, 0, 0);
        }
    }

    public static void addSurrounding(Collection<ChunkCoordinates> set, int i, int j, int k){
        set.add(new ChunkCoordinates(i + 1, j, k));
        set.add(new ChunkCoordinates(i - 1, j, k));
        set.add(new ChunkCoordinates(i, j + 1, k));
        set.add(new ChunkCoordinates(i, j - 1, k));
        set.add(new ChunkCoordinates(i, j, k + 1));
        set.add(new ChunkCoordinates(i, j, k - 1));

        set.add(new ChunkCoordinates(i + 1, j + 1, k));
        set.add(new ChunkCoordinates(i - 1, j + 1, k));
        set.add(new ChunkCoordinates(i + 1, j - 1, k));
        set.add(new ChunkCoordinates(i - 1, j - 1, k));
        set.add(new ChunkCoordinates(i, j + 1, k + 1));
        set.add(new ChunkCoordinates(i, j + 1, k - 1));
        set.add(new ChunkCoordinates(i, j - 1, k + 1));
        set.add(new ChunkCoordinates(i, j - 1, k - 1));
        set.add(new ChunkCoordinates(i + 1, j, k + 1));
        set.add(new ChunkCoordinates(i - 1, j, k + 1));
        set.add(new ChunkCoordinates(i + 1, j, k - 1));
        set.add(new ChunkCoordinates(i - 1, j, k - 1));
    }

    private static void expandPortal(World world, int i, int j, int k, Collection<ChunkCoordinates> set, Stack<ChunkCoordinates> created){
        if(!world.func_147437_c(i, j, k)) return;

        int score = isValidLinkPortalBlock(world.func_147439_a(i + 1, j, k)) + isValidLinkPortalBlock(world.func_147439_a(i - 1, j, k)) + isValidLinkPortalBlock(world.func_147439_a(i, j + 1, k)) + isValidLinkPortalBlock(world.func_147439_a(i, j - 1, k)) + isValidLinkPortalBlock(world.func_147439_a(i, j, k + 1)) + isValidLinkPortalBlock(world.func_147439_a(i, j, k - 1));
        if(score > 1){
            world.func_147465_d(i, j, k, NailedBlocks.portal, 0, 0);
            created.push(new ChunkCoordinates(i, j, k));
            addSurrounding(set, i, j, k);
        }
    }

    private static void directPortal(World world, int i, int j, int k, int meta, List<ChunkCoordinates> blocks, List<ChunkCoordinates> portals){
        if(isValidLinkPortalBlock(world.func_147439_a(i, j, k)) == 0) return;
        if(world.getBlockMetadata(i, j, k) != 0) return;
        world.setBlockMetadataWithNotify(i, j, k, meta, 0);
        if(world.func_147439_a(i, j, k) == NailedBlocks.portal)
            portals.add(new ChunkCoordinates(i, j, k));
        else
            blocks.add(new ChunkCoordinates(i, j, k));
    }

    private static void depolarize(World world, int i, int j, int k, List<ChunkCoordinates> blocks){
        Block block = world.func_147439_a(i, j, k);
        if(isValidLinkPortalBlock(block) == 0) return;
        if(world.getBlockMetadata(i, j, k) == 0) return;
        world.setBlockMetadataWithNotify(i, j, k, 0, 0);
        if((block == NailedBlocks.portal) && (!BlockPortal.isValidPortal(world, i, j, k))){
            world.func_147465_d(i, j, k, Blocks.air, 0, 2);
        }
        blocks.add(new ChunkCoordinates(i, j, k));
    }

    public static int isValidLinkPortalBlock(Block block){
        if(block == NailedBlocks.portalCrystal) return 1;
        if(block == NailedBlocks.portal) return 1;
        return 0;
    }

    public static TileEntity getTileEntity(IBlockAccess blockaccess, int x, int y, int z){
        HashSet<ChunkCoordinates> visited = Sets.newHashSet();
        Block block = blockaccess.func_147439_a(x, y, z);
        while(block != NailedBlocks.portalController){
            if(isValidLinkPortalBlock(block) == 0) return null;
            ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
            if(!visited.add(pos)){
                return null;
            }
            int meta = blockaccess.getBlockMetadata(x, y, z);
            if(meta == 0)
                return null;
            if(meta == 1)
                y--;
            else if(meta == 2)
                y++;
            else if(meta == 3)
                z--;
            else if(meta == 4)
                z++;
            else if(meta == 5)
                x--;
            else if(meta == 6)
                x++;
            else{
                return null;
            }
            block = blockaccess.func_147439_a(x, y, z);
        }
        return blockaccess.func_147438_o(x, y, z);
    }
}
