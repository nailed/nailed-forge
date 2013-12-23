package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import jk_5.nailed.map.Map;
import jk_5.nailed.util.ChatColor;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandKickall extends NailedCommand {

    @Override
    public String getCommandName(){
        return "kickall";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        String reason = Joiner.on(" ").join(args);
        for(EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList){
            player.playerNetServerHandler.kickPlayerFromServer("[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] " + reason);
        }
    }
}
