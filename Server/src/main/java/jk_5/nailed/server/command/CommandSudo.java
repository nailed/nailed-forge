package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
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
public class CommandSudo extends NailedCommand {

    @Override
    public String getCommandName(){
        return "sudo";
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
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings){
        if(strings.length != 1) return Arrays.asList();
        return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
    }
}
