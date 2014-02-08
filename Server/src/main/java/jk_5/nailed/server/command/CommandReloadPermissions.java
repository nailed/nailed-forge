package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.permissions.NailedPermissionFactory;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.PermissionsManager;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadPermissions extends NailedCommand {

    public CommandReloadPermissions(){
        super("reloadpermissions");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        PermBuilderFactory<?> factory = PermissionsManager.getPermFactory();
        if(factory instanceof NailedPermissionFactory){
            NailedPermissionFactory permFactory = (NailedPermissionFactory) factory;
            permFactory.readConfig();
        }
    }
}
