package jk_5.nailed.server.command;

import jk_5.nailed.util.ChatColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandKickall extends CommandBase {

    @Override
    public String getCommandName(){
        return "kickall";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/kickall <reason> - Kick all the players";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] args){
        for(EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList){
            player.playerNetServerHandler.kickPlayerFromServer("[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] " + args[0]);
        }
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
