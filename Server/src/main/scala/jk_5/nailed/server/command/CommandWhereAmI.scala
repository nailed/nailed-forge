package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandWhereAmI extends ScalaCommand {

  val name = "whereami"
  val usage = "/whereami - Shows in which map you are"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val comp = new ChatComponentText("You are in map " + map.getSaveFileName)
    comp.getChatStyle.setColor(EnumChatFormatting.GOLD)
    sender.addChatMessage(comp)
  }
}
