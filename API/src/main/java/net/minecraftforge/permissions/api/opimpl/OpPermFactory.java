package net.minecraftforge.permissions.api.opimpl;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.PermReg;
import net.minecraftforge.permissions.api.context.*;

import java.util.List;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class OpPermFactory implements PermBuilderFactory<Builder> {

    private static final IContext GLOBAL = new IContext() {};
    static Set<String> opPerms = Sets.newTreeSet();
    static Set<String> deniedPerms = Sets.newTreeSet();
    static Set<String> allowedPerms = Sets.newTreeSet();

    @Override
    public Builder builder(){
        return new Builder();
    }

    @Override
    public Builder builder(String username, String permNode){
        return new Builder().setUserName(username).setPermNode(permNode);
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player){
        return new PlayerContext(player);
    }

    @Override
    public IContext getDefaultContext(TileEntity te){
        return new TileEntityContext(te);
    }

    @Override
    public IContext getDefaultContext(ILocation loc){
        return new Point(loc);
    }

    @Override
    public IContext getDefaultContext(Entity entity){
        return new EntityContext(entity);
    }

    @Override
    public IContext getDefaultContext(World world){
        return new WorldContext(world);
    }

    @Override
    public IContext getGlobalContext(){
        return GLOBAL;
    }

    @Override
    public IContext getDefaultContext(Object object){
        if(object instanceof EntityLivingBase){
            return new EntityLivingContext((EntityLivingBase) object);
        }else{
            return GLOBAL;
        }
    }

    @Override
    public void registerPermissions(List<PermReg> perms){
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

    private static boolean isRegistered(String node){
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }
}
