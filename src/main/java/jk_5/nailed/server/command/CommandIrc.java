package jk_5.nailed.server.command;

import jk_5.nailed.util.ChatColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandIrc extends CommandBase {

    @Override
    public String getCommandName() {
        return "irc";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/irc Commands for interacting with irc";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] input) {
        if(input.length == 0) throw new CommandException("See " + ChatColor.GOLD + "/irc help" + ChatColor.RED + " for more information");
        if(input.length == 1){
            if(input[0].equalsIgnoreCase("help")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Command usage for /irc"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc connect <host> [port] [serverpass] - Connects to a server, and disconnects from the current server if already connected"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc disconnect - Disconnects from current server"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/irc join <channel> [channelpass] - Joins the specified channel"));
            }else if(input[0].equalsIgnoreCase("connect")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }else if(input[0].equalsIgnoreCase("disconnect")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }else if(input[0].equalsIgnoreCase("join")){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("Not yet implemented"));
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
