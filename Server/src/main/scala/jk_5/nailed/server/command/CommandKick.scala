package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.NailedAPI
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import net.minecraft.server.MinecraftServer

/**
 * No description given
 *
 * @author jk-5
 */
object CommandKick extends ScalaCommand {

  val name = "kick"
  val usage = "/kick <player> [reason]"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length == 0) throw new WrongUsageException("/kick <player> [reason]")
    val target = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
    val reason = if(args.length > 1){
      val newArray = new Array[String](args.length - 1)
      System.arraycopy(args, 1, newArray, 0, newArray.length)
      newArray.mkString(" ")
    }else "No reason given"

    target.kick("Kicked by " + sender.getCommandSenderName + ". Reason: " + reason)

    var msg = new ChatComponentText("Player " + target.getUsername + " was kicked by " + sender.getCommandSenderName)
    msg.getChatStyle.setColor(EnumChatFormatting.RED)
    MinecraftServer.getServer.getConfigurationManager.sendChatMsg(msg)
    msg = new ChatComponentText("Reason: " + reason)
    msg.getChatStyle.setColor(EnumChatFormatting.RED)
    MinecraftServer.getServer.getConfigurationManager.sendChatMsg(msg)

    msg = new ChatComponentText("Successfully kicked player " + target.getUsername)
    msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
    sender.addChatMessage(msg)
  }
}
