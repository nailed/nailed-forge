package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.base.*;

import net.minecraft.command.*;
import net.minecraft.server.*;

import cpw.mods.fml.common.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSudo extends NailedCommand {

    public CommandSudo() {
        super("sudo");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
        args[0] = null;
        String cmd = Joiner.on(" ").skipNulls().join(args);
        if(player != null){
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player.getEntity(), cmd);
        }else{
            throw new CommandException("Unknown player");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getUsernameOptions(args);
        }else if(args.length == 2){
            Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            return getOptions(args, (Iterable<String>) MinecraftServer.getServer().getCommandManager().getPossibleCommands(target.getEntity()));
        }else if(args.length > 2){
            Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            ICommand cmd = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(args[1]);
            if(cmd == null){
                return null;
            }
            String[] newArgs = new String[args.length - 2];
            System.arraycopy(args, 2, newArgs, 0, args.length - 2);
            return (List<String>) cmd.addTabCompletionOptions(target.getEntity(), newArgs);
        }else{
            return null;
        }
    }
}