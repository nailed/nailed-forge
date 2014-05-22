package net.minecraftforge.permissions.api.opimpl;

import net.minecraft.server.*;
import net.minecraft.server.integrated.*;

import cpw.mods.fml.common.*;

import net.minecraftforge.permissions.api.*;
import net.minecraftforge.permissions.api.context.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class Builder implements PermBuilder<Builder> {

    private IContext user;
    private IContext target;
    private String username;
    private String node;

    @Override
    public boolean check() {
        if(OpPermFactory.deniedPerms.contains(node)){
            return false;
        }else if(OpPermFactory.allowedPerms.contains(node)){
            return true;
        }else if(OpPermFactory.opPerms.contains(node)){
            return isOp(username);
        }else{
            throw new UnregisterredPermissionException(node);
        }
    }

    @Override
    public Builder setUserName(String name) {
        this.username = name;
        return this;
    }

    @Override
    public Builder setPermNode(String node) {
        this.node = node;
        return this;
    }

    @Override
    public Builder setTargetContext(IContext context) {
        this.target = context;
        return this;
    }

    @Override
    public Builder setUserContext(IContext context) {
        this.user = context;
        return this;
    }

    private static boolean isOp(String username) {
        MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

        if(server.isSinglePlayer()){
            if(server instanceof IntegratedServer){
                return server.getServerOwner().equalsIgnoreCase(username);
            }else{
                return server.getConfigurationManager().getOps().contains(username);
            }
        }

        return server.getConfigurationManager().getOps().contains(username);
    }
}
