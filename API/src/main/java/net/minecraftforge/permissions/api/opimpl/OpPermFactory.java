package net.minecraftforge.permissions.api.opimpl;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.dispenser.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import cpw.mods.fml.common.*;

import net.minecraftforge.permissions.api.*;
import net.minecraftforge.permissions.api.context.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class OpPermFactory implements PermBuilderFactory<Builder> {

    static Set<String> opPerms = Sets.newTreeSet();
    static Set<String> deniedPerms = Sets.newTreeSet();
    static Set<String> allowedPerms = Sets.newTreeSet();
    private static final IContext GLOBAL = new IContext() {
    };

    @Override
    public Builder builder() {
        return new Builder();
    }

    @Override
    public Builder builder(String username, String permNode) {
        return new Builder().setUserName(username).setPermNode(permNode);
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player) {
        return new PlayerContext(player);
    }

    @Override
    public IContext getDefaultContext(TileEntity te) {
        return new TileEntityContext(te);
    }

    @Override
    public IContext getDefaultContext(ILocation loc) {
        return new Point(loc);
    }

    @Override
    public IContext getDefaultContext(Entity entity) {
        return new EntityContext(entity);
    }

    @Override
    public IContext getDefaultContext(World world) {
        return new WorldContext(world);
    }

    @Override
    public IContext getGlobalContext() {
        return GLOBAL;
    }

    @Override
    public IContext getDefaultContext(Object object) {
        if(object instanceof EntityLivingBase){
            return new EntityLivingContext((EntityLivingBase) object);
        }else{
            return GLOBAL;
        }
    }

    @Override
    public void registerPermissions(List<PermReg> perms) {
        for(PermReg entry : perms){
            if(isRegistered(entry.key)){
                continue;
            }
            FMLLog.info("Registering permission node %s with default value %s", entry.key, entry.role);
            switch(entry.role){
                case OP:
                    opPerms.add(entry.key);
                    break;
                case NONOP:
                    allowedPerms.add(entry.key);
                    break;
                case FALSE:
                    deniedPerms.add(entry.key);
                    break;
                case TRUE:
                    allowedPerms.add(entry.key);
                    break;
            }
        }
    }

    private static boolean isRegistered(String node) {
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }
}
