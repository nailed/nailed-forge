package jk_5.nailed.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import net.minecraft.block.Block;

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
    public static BlockStatEmitter statEmitter;
    public static BlockPortal portal;

    public static void init(){
        invisibleWall = new BlockInvisibleWall();
        portalCrystal = new BlockPortalCrystal();
        portalController = new BlockPortalController();
        portal = new BlockPortal();
        statEmitter = new BlockStatEmitter();

        registerBlock(invisibleWall);
        registerBlock(portalCrystal);
        registerBlock(portalController);
        registerBlock(portal);
        registerBlock(statEmitter);

        GameRegistry.registerTileEntity(TileEntityPortalController.class, "nailed.portalController");
        GameRegistry.registerTileEntity(TileEntityStatEmitter.class, "nailed.statEmitter");
    }

    private static void registerBlock(Block block){
        GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
    }
}
