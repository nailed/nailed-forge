package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

/**
 * Created by matthias on 22-5-14.
 */
public class CommandWhereAmI extends NailedCommand {
    public CommandWhereAmI(){
        super("whereami");
    }

    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        Player player = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayerMP) sender);
        sender.addChatMessage(new ChatComponentText("{'text':'You are in map ','extra':[{'text':" + player.getCurrentMap().getName() + "','color':'green','bold':'true'}]}"));
    }
}
