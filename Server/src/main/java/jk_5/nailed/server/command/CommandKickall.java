package jk_5.nailed.server.command;

import com.google.common.base.*;

import net.minecraft.command.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandKickall extends NailedCommand {

    public CommandKickall() {
        super("kickall");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        String reason = "[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] " + Joiner.on(" ").join(args);
        for(Player player : NailedAPI.getPlayerRegistry().getOnlinePlayers()){
            player.kick(reason);
        }
    }
}
