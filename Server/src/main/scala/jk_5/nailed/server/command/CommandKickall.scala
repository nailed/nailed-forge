package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.util.ChatColor
import jk_5.nailed.api.NailedAPI
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandKickall extends ScalaCommand {

  val name = "kickall"
  val usage = "/kickall <message> - Kicks all the players with the specified message"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length == 0) throw new WrongUsageException("Usage: /kickall <message>")
    val reason = "[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] " + args.mkString(" ")
    NailedAPI.getPlayerRegistry.getOnlinePlayers.foreach(_.kick(reason))
  }
}
