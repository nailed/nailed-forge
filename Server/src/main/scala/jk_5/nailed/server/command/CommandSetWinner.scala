package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.{PossibleWinner, Map}
import jk_5.nailed.api.NailedAPI
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import scala.collection.mutable
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSetWinner extends ScalaCommand {

  val name = "setwinner"
  val usage = "/setwinner <winner> - Sets the specified team or player as the winner of the game"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length == 0) throw new WrongUsageException("/setwinner <winner>")
    var winner: PossibleWinner = map.getTeamManager.getTeam(args(0))
    if(winner == null){
      val player = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
      if(player.getCurrentMap != map) throw new CommandException("Player " + args(0) + " is not in this map")
      else winner = player
    }
    if(winner == null) throw new CommandException(args(0) + " is not a player nor team")

    map.getGameManager.setWinner(winner)

    val msg = new ChatComponentText("Winner set to " + args(0))
    msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
    sender.addChatMessage(msg)
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1){
      val map = NailedAPI.getMapLoader.getMap(sender.getEntityWorld)
      val suggestions = mutable.ListBuffer[String]()
      suggestions ++= map.getTeamManager.getTeams.map(_.getTeamId)
      suggestions ++= map.getPlayers.map(_.getUsername)
      getOptions(args, suggestions)
    }else null
}
