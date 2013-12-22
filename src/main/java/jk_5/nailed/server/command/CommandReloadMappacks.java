package jk_5.nailed.server.command;

import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMappacks extends CommandBase {

    @Override
    public String getCommandName(){
        return "reloadmappacks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "/reloadmappacks - Reloads the mappacks from the mappacks folder";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        MapLoader.instance().loadMappacks();
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Successfully loaded " + MapLoader.instance().getMappacks().size() + " mappacks!").setColor(EnumChatFormatting.GREEN));
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}