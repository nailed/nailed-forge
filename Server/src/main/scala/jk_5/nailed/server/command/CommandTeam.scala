package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.util.ScalaUtils._
import jk_5.nailed.api.NailedAPI
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandTeam extends ScalaCommand {

  val name = "team"
  val usage = "/team join <username> <team>"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) = if(args.length != 0) caseInsensitiveMatch(args(0)) {
    case "join" =>
      if(args.length == 1) throw new WrongUsageException("/team join <username> <team>")
      val player = NailedAPI.getPlayerRegistry.getPlayer(NailedCommand.getTargetPlayer(sender, args(1)))
      if(player == null) throw new CommandException("Unknown username " + args(1))
      if(args.length == 2) throw new WrongUsageException(s"/team join ${args(1)} <team>")
      Option(map.getTeamManager.getTeam(args(2))) match {
        case Some(team) =>
          if(args.length == 3){
            map.getTeamManager.setPlayerTeam(player, team)
            val msg = new ChatComponentText(s"Player ${player.getUsername} is now in team ${team.getColoredName}")
            msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
            map.broadcastChatMessage(msg)
          }else throw new WrongUsageException("/team join <username> <team>")
        case None => throw new CommandException("Unknown team name " + args(2))
      }
    case _ => throw new WrongUsageException("/team join <username> <team>")
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1) getOptions(args, "join")
    else if(args.length == 2){
      if(args(0).equalsIgnoreCase("join")) getUsernameOptions(args) else null
    }else if(args.length == 3){
      if(args(0).equalsIgnoreCase("join")){
        getOptions(args, NailedAPI.getMapLoader.getMap(sender.getEntityWorld).getTeamManager.getTeams.map(_.getTeamId))
      }else null
    }else null
}
