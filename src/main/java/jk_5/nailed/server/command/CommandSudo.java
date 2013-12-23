package jk_5.nailed.server.command;

import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSudo extends CommandBase {

    @Override
    public String getCommandName(){
        return "sudo";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/sudo <player> <command> - Execute a command as another player";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++){
            cmd.append(args[i]);
            cmd.append(" ");
        }
        Player player = PlayerRegistry.instance().getPlayer(args[0]);
        if (player != null){
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player.getEntity(), cmd.toString());
        }else{
            throw new CommandException("Unknown player");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings){
        if(strings.length != 1) return Arrays.asList();
        return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
