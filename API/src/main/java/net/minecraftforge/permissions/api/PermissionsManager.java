package net.minecraftforge.permissions.api;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.opimpl.OpPermFactory;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PermissionsManager {

    private static boolean wasSet = false;
    private static List<PermReg> central = Lists.newArrayList(); // the canonical list of permissions, given to the factory at serverStarted
    private static final PermBuilderFactory DEFAULT = new OpPermFactory();
    private static PermBuilderFactory FACTORY;

    public static boolean checkPerm(EntityPlayer player, String node){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        IContext context = FACTORY.getDefaultContext(player);
        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(context)
                .setTargetContext(context)
                .check();
    }

    public static boolean checkPerm(EntityPlayer player, String node, Entity targetContext){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(FACTORY.getDefaultContext(player))
                .setTargetContext(FACTORY.getDefaultContext(targetContext))
                .check();
    }

    public static boolean checkPerm(EntityPlayer player, String node, ILocation targetContext){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(FACTORY.getDefaultContext(player))
                .setTargetContext(FACTORY.getDefaultContext(targetContext))
                .check();
    }

    public static PermBuilder getPerm(EntityPlayer player, String node){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        IContext context = FACTORY.getDefaultContext(player);
        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(context)
                .setTargetContext(context);
    }

    public static PermBuilder getPerm(EntityPlayer player, String node, Entity targetContext){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(FACTORY.getDefaultContext(player))
                .setTargetContext(FACTORY.getDefaultContext(targetContext));
    }

    public static PermBuilder getPerm(EntityPlayer player, String node, ILocation targetContext){
        if(player instanceof FakePlayer){
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");
        }

        return FACTORY.builder(player.getGameProfile().getName(), node)
                .setUserContext(FACTORY.getDefaultContext(player))
                .setTargetContext(FACTORY.getDefaultContext(targetContext));
    }

    public static PermBuilder getPerm(String username, String node, TileEntity userContext){
        return FACTORY.builder(username, node).setUserContext(FACTORY.getDefaultContext(userContext));
    }

    public static PermBuilder getPerm(String username, String node){
        return FACTORY.builder(username, node);
    }

    public static PermBuilderFactory getPermFactory(){
        return FACTORY;
    }

    /**
     * Register a new permissions handler. Do not use unless you know what you're doing.
     *
     * @param factory Your permissions handler class, implementing {@link net.minecraftforge.permissions.api.PermBuilderFactory}
     * @param modID   Your mod ID
     * @throws IllegalStateException if there is already a permissions handler set.
     */
    public static void setPermFactory(PermBuilderFactory factory, String modID) throws IllegalStateException{
        if(factory == null){
            FACTORY = DEFAULT;
            wasSet = false;
        }else if(wasSet){
            throw new IllegalStateException(String.format("Mod %s tried to register a permission system when one has already been set!", modID));
        }else{
            FMLLog.info("Registering permission handler %s from mod %s", factory.toString(), modID);
            FACTORY = factory;
            wasSet = true;
        }
    }

    /**
     * Register permissions for checking with the permission handler
     *
     * @param perms A permission, packed into a {@link net.minecraftforge.permissions.api.PermReg}
     */
    public static void registerPermission(PermReg perms){
        central.add(perms);
    }

    public static void registerPermission(String node){
        registerPermission(node, RegisteredPermValue.FALSE);
    }

    public static void registerPermission(String node, RegisteredPermValue defaultValue){
        central.add(new PermReg(node, defaultValue, null));
    }

    public static void addPermissionsToFactory(){
        FACTORY.registerPermissions(central);
    }
}
