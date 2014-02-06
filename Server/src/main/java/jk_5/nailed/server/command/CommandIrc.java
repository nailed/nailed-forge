package jk_5.nailed.server.command;

import jk_5.nailed.api.ChatColor;
import jk_5.nailed.api.map.Map;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
                sender.addChatMessage(new ChatComponentText("Command usage for /irc"));
                sender.addChatMessage(new ChatComponentText("/irc connect <host> [port] [serverpass] - Connects to a server, and disconnects from the current server if already connected"));
                sender.addChatMessage(new ChatComponentText("/irc disconnect - Disconnects from current server"));
                sender.addChatMessage(new ChatComponentText("/irc join <channel> [channelpass] - Joins the specified channel"));
            }else if(args[0].equalsIgnoreCase("connect")){
                sender.addChatMessage(new ChatComponentText("Not yet implemented"));
            }else if(args[0].equalsIgnoreCase("disconnect")){
                sender.addChatMessage(new ChatComponentText("Not yet implemented"));
            }else if(args[0].equalsIgnoreCase("join")){
                sender.addChatMessage(new ChatComponentText("Not yet implemented"));
            }
        }
    }
}
