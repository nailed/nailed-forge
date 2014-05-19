package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.util.ChatColor;
import net.minecraft.command.ICommandSender;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandKickall extends NailedCommand {

    public CommandKickall(){
        super("kickall");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        String reason = "[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] " + Joiner.on(" ").join(args);
        for(Player player : NailedAPI.getPlayerRegistry().getOnlinePlayers()){
            player.kick(reason);
        }
    }
}
