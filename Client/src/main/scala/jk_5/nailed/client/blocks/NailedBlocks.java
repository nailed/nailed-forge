package jk_5.nailed.client.blocks;

import net.minecraft.item.*;

import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.registry.*;

import jk_5.nailed.client.blocks.tileentity.*;
import jk_5.nailed.client.item.*;
import jk_5.nailed.client.render.tileentity.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class NailedBlocks {

    public static BlockInvisibleWall invisibleWall;
    public static BlockPortalCrystal portalCrystal;
    public static BlockPortalController portalController;
    public static BlockStat stat;
    public static BlockPortal portal;
    public static BlockLight light;

    private NailedBlocks(){

    }

    public static void init(){
        invisibleWall = new BlockInvisibleWall();
        portalCrystal = new BlockPortalCrystal();
        portalController = new BlockPortalController();
        portal = new BlockPortal();
        stat = new BlockStat();
        light = new BlockLight();

        registerBlock(invisibleWall, ItemBlockMulti.class);
        registerBlock(portalCrystal);
        registerBlock(portalController);
        registerBlock(portal);
        registerBlock(stat, ItemBlockMulti.class);
        registerBlock(light);

        GameRegistry.registerTileEntity(TileEntityPortalController.class, "nailed.portalController");
        GameRegistry.registerTileEntity(TileEntityStatEmitter.class, "nailed.stat");
        GameRegistry.registerTileEntity(TileEntityStatModifier.class, "nailed.statmodifier");
        GameRegistry.registerTileEntity(TileEntitySky.class, "nailed.sky");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySky.class, TileEntitySkyRenderer$.MODULE$);
    }

    private static void registerBlock(NailedBlock block){
        GameRegistry.registerBlock(block, block.getRegisteredName());
    }

    private static void registerBlock(NailedBlock block, Class<? extends ItemBlock> item){
        GameRegistry.registerBlock(block, item, block.getRegisteredName());
    }
}
