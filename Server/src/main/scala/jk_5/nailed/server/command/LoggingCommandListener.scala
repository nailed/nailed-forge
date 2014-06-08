package jk_5.nailed.server.command

import org.apache.logging.log4j.{LogManager, MarkerManager}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.CommandEvent
import net.minecraft.command.server.CommandBlockLogic
import jk_5.nailed.api.NailedAPI

/**
 * No description given
 *
 * @author jk-5
 */
object LoggingCommandListener {

  final val logger = LogManager.getLogger
  final val marker = MarkerManager.getMarker("commands")

  @SubscribeEvent def onEvent(event: CommandEvent){
    if(event.sender.isInstanceOf[CommandBlockLogic]) return
    val args = event.parameters.mkString(" ")
    logger.info(marker, "[{}] /{} {}", event.sender.getCommandSenderName, event.command.getCommandName, args)
    Option(NailedAPI.getPlayerRegistry.getPlayerByUsername("jk_5")) match {
      case Some(p) => p.sendChat("[" + event.sender.getCommandSenderName + "] /" + event.command.getCommandName + " " + args)
      case None =>
    }
  }
}
