package jk_5.nailed.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import jk_5.nailed.item.ItemBlockMulti;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

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
    public static BlockLight light;

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
        registerBlock(light, ItemBlockMulti.class);

        GameRegistry.registerTileEntity(TileEntityPortalController.class, "nailed.portalController");
        GameRegistry.registerTileEntity(TileEntityStatEmitter.class, "nailed.stat");
        GameRegistry.registerTileEntity(TileEntityStatModifier.class, "nailed.statmodifier");

        PermissionsManager.registerPermission(TileEntityStatEmitter.PERMNODE, RegisteredPermValue.OP);
    }

    private static void registerBlock(NailedBlock block){
        GameRegistry.registerBlock(block, block.getRegisteredName());
    }

    private static void registerBlock(NailedBlock block, Class<? extends ItemBlock> item){
        GameRegistry.registerBlock(block, item, block.getRegisteredName());
    }
}
