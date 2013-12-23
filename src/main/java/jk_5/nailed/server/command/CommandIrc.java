package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.util.ChatColor;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandIrc extends NailedCommand {

    @Override
    public String getCommandName() {
        return "irc";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0) throw new CommandException("See " + ChatColor.GOLD + "/irc help" + ChatColor.RED + " for more information");
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Command usage for /irc"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc connect <host> [port] [serverpass] - Connects to a server, and disconnects from the current server if already connected"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc disconnect - Disconnects from current server"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc join <channel> [channelpass] - Joins the specified channel"));
            }else if(args[0].equalsIgnoreCase("connect")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }else if(args[0].equalsIgnoreCase("disconnect")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }else if(args[0].equalsIgnoreCase("join")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }
        }
    }
}
