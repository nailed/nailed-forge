package jk_5.nailed.server.command

import net.minecraft.command.{CommandBase, ICommandSender}
import jk_5.nailed.api.map.Map
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import jk_5.nailed.util.ScalaUtils._
import jk_5.nailed.api.NailedAPI

/**
 * No description given
 *
 * @author jk-5
 */
object CommandTime extends ScalaCommand {

  val name = "time"
  val usage = "/time [set] [day|night|0-23999]"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) = if(args.length > 0){
    caseInsensitiveMatch(args(0)) {
      case "set" =>
        if(args.length > 1){
          map.getWorld.setWorldTime(caseInsensitiveMatch(args(1)) {
            case "day" => 6000
            case "night" => 18000
            case t => CommandBase.parseIntBounded(sender, args(1), 0, 23999)
          })
        } throw new WrongUsageException("/time [set] [day|night|0-23999]")
      case _ =>
        try{
          val number = CommandBase.parseInt(sender, args(0))
          val map = NailedAPI.getMapLoader.getMap(number)
          if(map != null){
            val msg = new ChatComponentText("Current time in " + map.getSaveFileName + ": " + map.getWorld.getWorldTime)
            msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
            sender.addChatMessage(msg)
          }else throw new CommandException("Unknown Map")
        }catch{
          case e: Exception =>
        }
    }
  }else{
    val msg = new ChatComponentText(s"Current time in ${map.getSaveFileName}: ${map.getWorld.getWorldTime}")
    msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
    sender.addChatMessage(msg)
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1) getOptions(args, "set")
    else if(args.length == 2) getOptions(args, "day", "night")
    else null
}
