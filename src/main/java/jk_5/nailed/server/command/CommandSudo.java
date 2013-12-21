package jk_5.nailed.server.command;

import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

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
        return "/sudo <command> - Execute a command as another player";
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
    public int compareTo(Object o){
        return 0;
    }
}
