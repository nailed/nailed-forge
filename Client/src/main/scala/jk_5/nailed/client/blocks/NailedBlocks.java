package jk_5.nailed.client.blocks;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.client.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.client.blocks.tileentity.TileEntitySky;
import jk_5.nailed.client.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.client.blocks.tileentity.TileEntityStatModifier;
import jk_5.nailed.client.item.ItemBlockMulti;
import jk_5.nailed.client.render.tileentity.TileEntitySkyRenderer$;
import net.minecraft.item.ItemBlock;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlocks {

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

        registerBlock(invisibleWall, ItemBlockMulti.class);
        registerBlock(portalCrystal);
        registerBlock(portalController);
        registerBlock(portal);
        registerBlock(stat, ItemBlockMulti.class);

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
