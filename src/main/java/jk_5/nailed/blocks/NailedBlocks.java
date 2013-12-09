package jk_5.nailed.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.item.ItemBlockMulti;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlocks {

    public static CreativeTabNailed creativeTab = new CreativeTabNailed();

    public static BlockInvisibleWall invisibleWall;
    public static BlockPortalCrystal portalCrystal;
    public static BlockPortalController portalController;
    public static BlockStat stat;
    public static BlockPortal portal;

    public static void init(){
        invisibleWall = new BlockInvisibleWall();
        portalCrystal = new BlockPortalCrystal();
        portalController = new BlockPortalController();
        portal = new BlockPortal();
        stat = new BlockStat();

        registerBlock(invisibleWall);
        registerBlock(portalCrystal);
        registerBlock(portalController);
        registerBlock(portal);
        registerBlock(stat, ItemBlockMulti.class);

        GameRegistry.registerTileEntity(TileEntityPortalController.class, "nailed.portalController");
        GameRegistry.registerTileEntity(TileEntityStatEmitter.class, "nailed.stat");
    }

    private static void registerBlock(Block block){
        GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
    }

    private static void registerBlock(Block block, Class<? extends ItemBlock> item){
        GameRegistry.registerBlock(block, item, block.getUnlocalizedName().substring(5));
    }
}
