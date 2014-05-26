package jk_5.nailed.server.command;

import com.google.common.base.*;

import org.apache.logging.log4j.*;

import net.minecraft.command.server.*;

import cpw.mods.fml.common.eventhandler.*;

import net.minecraftforge.event.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class LoggingCommandListener {

    private Logger logger = LogManager.getLogger();
    private Marker marker = MarkerManager.getMarker("commands");

    @SubscribeEvent
    public void onEvent(CommandEvent event) {
        if(event.sender instanceof CommandBlockLogic){
            return;
        }
        String args = Joiner.on(' ').join(event.parameters);
        logger.info(marker, "[{}] /{} {}", event.sender.getCommandSenderName(), event.command.getCommandName(), args);
        Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername("jk_5");
        if(p != null){
            p.sendChat(event.sender.getCommandSenderName() + " - /" + event.command.getCommandName() + " " + args);
        }
    }
}
