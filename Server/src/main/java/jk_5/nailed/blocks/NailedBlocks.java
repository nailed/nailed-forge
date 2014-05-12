package jk_5.nailed.blocks;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import jk_5.nailed.item.ItemBlockMulti;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
    public static BlockCommandBlockOverride commandBlock;

    public static void init() {
        invisibleWall = new BlockInvisibleWall();
        portalCrystal = new BlockPortalCrystal();
        portalController = new BlockPortalController();
        portal = new BlockPortal();
        stat = new BlockStat();
        light = new BlockLight();
        commandBlock = new BlockCommandBlockOverride();

        registerBlock(invisibleWall, ItemBlockMulti.class);
        registerBlock(portalCrystal);
        registerBlock(portalController);
        registerBlock(portal);
        registerBlock(stat, ItemBlockMulti.class);
        registerBlock(light);

        replaceVanillaBlock(Block.getIdFromBlock(Blocks.command_block), "command_block", commandBlock,Blocks.command_block);

        GameRegistry.registerTileEntity(TileEntityPortalController.class, "nailed.portalController");
        GameRegistry.registerTileEntity(TileEntityStatEmitter.class, "nailed.stat");
        GameRegistry.registerTileEntity(TileEntityStatModifier.class, "nailed.statmodifier");

        PermissionsManager.registerPermission(TileEntityStatEmitter.PERMNODE, RegisteredPermValue.OP);
    }

    private static void registerBlock(NailedBlock block) {
        GameRegistry.registerBlock(block, block.getRegisteredName());
    }

    private static void registerBlock(NailedBlock block, Class<? extends ItemBlock> item) {
        GameRegistry.registerBlock(block, item, block.getRegisteredName());
    }

    public static void replaceVanillaBlock(int id, String name, Block block, Block vanilla) {
        try {
            ItemBlock ib = (ItemBlock) Item.getItemFromBlock(vanilla);

            //add block to registry
            Method method = ReflectionHelper.findMethod(FMLControlledNamespacedRegistry.class, null, new String[]{"addObjectRaw"}, Integer.TYPE, String.class, Object.class);
            method.invoke(Block.blockRegistry, id, "minecraft:" + name, block);

            //modify reference in Blocks class
            Field f = ReflectionHelper.findField(Blocks.class, name);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(null, block);

            if (ib != null) {
                f = ReflectionHelper.findField(ItemBlock.class, "field_150939_a");
                modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(ib, block);
            }

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
