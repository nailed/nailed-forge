package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import net.minecraftforge.event.CommandEvent;

/**
 * No description given
 *
 * @author jk-5
 */
public class LoggingCommandListener {

    @SubscribeEvent
    public void onEvent(CommandEvent event){
        if(event.sender.getCommandSenderName().equals("@")) return;
        Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername("Clank26");
        if(p != null){
            p.sendChat(event.sender.getCommandSenderName() + " - /" + event.command.getCommandName() + " " + Joiner.on(' ').join(event.parameters));
        }
    }
}
