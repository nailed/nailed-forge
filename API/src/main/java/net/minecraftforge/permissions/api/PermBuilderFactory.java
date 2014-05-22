package net.minecraftforge.permissions.api;

import java.util.*;

import net.minecraft.dispenser.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import net.minecraftforge.permissions.api.context.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PermBuilderFactory<T extends PermBuilder> {

    /**
     * This method should return a fresh unadulterated PermBuilder instance with no default values.
     *
     * @return a new instance of your PermBuilder.
     */
    T builder();

    /**
     * This method should return a PermBuilder instance with the username and PermNode set.
     *
     * @return a new instance of your PermBuilder.
     */
    T builder(String username, String permNode);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(EntityPlayer player);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(TileEntity te);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(ILocation loc);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Entity entity);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(World world);

    /**
     * @return A IContext signifying the Server as a whole.
     */
    IContext getGlobalContext();

    /**
     * At the very least, this method should return an anonymous instance of IContext.
     * This method should NEVER return null.
     *
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Object object);

    void registerPermissions(List<PermReg> perms);
}
