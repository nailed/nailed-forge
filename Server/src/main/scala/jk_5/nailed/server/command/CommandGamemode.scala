package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.api.player.Player
import jk_5.nailed.api.{Gamemode, NailedAPI}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandGamemode extends ScalaCommand {

  val name = "gamemode"
  val usage = "/gamemode <survival|creative|adventure> [player] - Changes your or another player's gamemode"
  override val aliases = Array("gm")

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length == 0) sender match {
      case e: EntityPlayer => toggleGamemode(NailedAPI.getPlayerRegistry.getPlayer(e))
      case _ => throw new CommandException("You are not a player")
    }else if(args.length == 1) sender match {
      case e: EntityPlayer =>
        val mode = fromString(args(0))
        if(mode.isEmpty) throw new CommandException("Unknown gamemode")
        NailedAPI.getPlayerRegistry.getPlayer(e).setGameMode(mode.get)
      case _ => throw new CommandException("You are not a player")
    }else if(args.length == 2){
      val players = NailedCommand.getPlayersList(sender, args(1))
      val newmode = fromString(args(0))
      if(newmode.isEmpty) throw new CommandException("Unknown gamemode")
      for(p <- players){
        val target = NailedAPI.getPlayerRegistry.getPlayer(p)
        if(target != null) target.setGameMode(newmode.get)
      }
    }
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]) = args.length match {
    case 1 => getOptions(args, "survival", "creative", "adventure")
    case 2 => getUsernameOptions(args)
    case _ => null
  }

  def fromString(s: String) =
    if(s.equalsIgnoreCase("survival") || s.equalsIgnoreCase("s") || s == "0") Some(Gamemode.SURVIVAL)
    else if(s.equalsIgnoreCase("creative") || s.equalsIgnoreCase("c") || s == "1") Some(Gamemode.CREATIVE)
    else if(s.equalsIgnoreCase("adventure") || s.equalsIgnoreCase("a") || s == "2") Some(Gamemode.ADVENTURE)
    else None

  def toggleGamemode(p: Player) = p.setGameMode(if(p.getGameMode == Gamemode.SURVIVAL) Gamemode.CREATIVE else Gamemode.SURVIVAL)
}
