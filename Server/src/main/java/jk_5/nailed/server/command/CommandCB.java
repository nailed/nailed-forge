package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.map.teleport.TeleportHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandCB extends NailedCommand {

    public CommandCB(){
        super("cb");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0) return;
        if(args[0].equals("chat")){
            String msg = Joiner.on(' ').join(args);
            msg = msg.substring(msg.indexOf(' ') + 1);
            map.broadcastChatMessage(new ChatComponentText(msg));
        }else if(args[0].equals("notify")){
            String msg = Joiner.on(' ').join(args);
            msg = msg.substring(msg.indexOf(' ') + 1);
            map.broadcastNotification(msg);
        }else if(args[0].equalsIgnoreCase("tpToRandomSpawn")){
            EntityPlayerMP[] players = getPlayersList(sender, args[1]);
            for(EntityPlayerMP player : players){
                TeleportHelper.travelEntity(player, map.getRandomSpawnpoint().teleport());
            }
        }
    }
}
