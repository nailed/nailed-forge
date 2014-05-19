package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSudo extends NailedCommand {

    public CommandSudo(){
        super("sudo");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
        args[0] = null;
        String cmd = Joiner.on(" ").skipNulls().join(args);
        if (player != null){
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player.getEntity(), cmd);
        }else{
            throw new CommandException("Unknown player");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings){
        if(strings.length == 1){
            return CommandBase.getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
        }else if(strings.length == 2){
            Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(strings[0]);
            return CommandBase.getListOfStringsFromIterableMatchingLastWord(strings, MinecraftServer.getServer().getCommandManager().getPossibleCommands(target.getEntity()));
        }else if(strings.length > 2){
            Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(strings[0]);
            ICommand cmd = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(strings[1]);
            if(cmd == null) return null;
            String[] newArgs = new String[strings.length - 2];
            System.arraycopy(strings, 2, newArgs, 0, strings.length - 2);
            return cmd.addTabCompletionOptions(target.getEntity(), newArgs);
        }else return null;
    }
}
