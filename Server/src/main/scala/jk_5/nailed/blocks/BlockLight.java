package jk_5.nailed.blocks;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockLight extends NailedBlock {

    public BlockLight() {
        super("light", Material.glass);

        this.disableStats();
        this.setBlockUnbreakable();
        this.setLightOpacity(0);
        this.setHardness(6000000.0F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ) {
        if(entity.getHeldItem() == null){
            int meta = world.getBlockMetadata(x, y, z);
            int oldMeta = meta;
            if(entity.isSneaking()){
                meta--;
            }else{
                meta++;
            }
            meta = Math.min(Math.max(meta, 0), 15);
            if(meta != oldMeta){
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.updateLightByType(EnumSkyBlock.Block, x, y, z);
                entity.addChatComponentMessage(new ChatComponentTranslation("tile.nailed.light.newLevel", meta));
            }
            return true;
        }
        return false;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public Block getReplacementBlock() {
        return Blocks.air;
    }

    @Override
    public int getReplacementMetadata() {
        return 0;
    }
}
