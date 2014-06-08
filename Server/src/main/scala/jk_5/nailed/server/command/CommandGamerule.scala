package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import net.minecraft.event.HoverEvent
import jk_5.nailed.util.Utils

/**
 * No description given
 *
 * @author jk-5
 */
object CommandGamerule extends ScalaCommand {

  val name = "gamerule"
  val usage = "/gamerule [rule] [new value] - List all gamerules, list one gamerule, or modify a gamerule"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val gameRules = map.getWorld.getGameRules
    args.length match {
      case 0 =>
        val base = new ChatComponentText("")
        val comp = new ChatComponentText("Available gamerules: ")
        comp.getChatStyle.setColor(EnumChatFormatting.GREEN)
        base.appendSibling(comp)
        var first = true
        for(rule <- gameRules.getRules){
          if(!first) base.appendText(", ") else first = false
          val c = new ChatComponentText(rule)
          c.getChatStyle.setColor(EnumChatFormatting.RESET)
          val tooltip = new ChatComponentText(rule + " = " + gameRules.getGameRuleStringValue(rule))
          c.getChatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip))
          base.appendSibling(c)
        }
        sender.addChatMessage(Utils.minifyChatComponent(base))
      case 1 =>
        if(!gameRules.hasRule(args(0))) throw new CommandException(s"Gamerule \"${args(0)}\" does not exist")
        sender.addChatMessage(new ChatComponentText(args(0) + " = " + gameRules.getGameRuleStringValue(args(0))))
      case 2 =>
        if(!gameRules.hasRule(args(0))) throw new CommandException(s"Gamerule \"${args(0)}\" does not exist")
        gameRules.setOrCreateGameRule(args(0), args(1))

        val msg = new ChatComponentText("Gamerule " + args(0) + " changed to " + args(1))
        msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
        sender.addChatMessage(msg)
      case _ => throw new WrongUsageException("Usage: /gamerule [rule] [new value]")
    }
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] = args.length match {
    case 1 => getOptions(args, sender.getEntityWorld.getGameRules.getRules)
    case 2 => getOptions(args, "true", "false")
    case _ => null
  }
}
