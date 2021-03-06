package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.NailedAPI
import net.minecraft.command.ICommandSender
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandGoto extends ScalaCommand {

  val name = "goto"
  val usage = "/goto <map> - Teleports you to the specified map"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    if(args.length == 0) throw new WrongUsageException("/goto <map>")
    var dest = NailedAPI.getMapLoader.getMap(args(0))
    if(dest == null){
      try{
        dest = NailedAPI.getMapLoader.getMap(Integer.parseInt(args(0)))
      }catch{
        case e: NumberFormatException => throw new CommandException("That map does not exist")
      }
    }
    if(dest == null) throw new CommandException("That map does not exist")
    sender.teleportToMap(dest)
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1) getOptions(args, NailedAPI.getMapLoader.getMaps.map(_.getSaveFileName).toList) else null
}
