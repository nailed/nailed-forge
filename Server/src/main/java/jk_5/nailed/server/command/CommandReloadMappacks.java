package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.ICommandSender;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMappacks extends NailedCommand {

    @Override
    public String getCommandName(){
        return "reloadmappacks";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        MapLoader.instance().loadMappacks();
        //FIXME
        //sender.func_145747_a(new ChatComponentText("Successfully loaded " + MapLoader.instance().getMappacks().size() + " mappacks!").setColor(EnumChatFormatting.GREEN));
    }
}
