package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.entity.player.EntityPlayer

/**
 * No description given
 *
 * @author jk-5
 */
object CommandHeal extends ScalaCommand {

  override val name = "heal"
  override val usage = "/heal [player] - Heals yourself or the player specified"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) =
    if(args.length == 0) sender match {
      case _: EntityPlayer if map.getGameManager.isGameRunning => throw new CommandException("You may not heal people while a game is running")
      case p: EntityPlayer => p.setHealth(20)
      case _ => throw new WrongUsageException("Usage: /heal <player>")
    }else if(args.length > 1) getPlayersList(sender, args(0)).foreach(_.setHealth(20))

  override def addAutocomplete(sender: ICommandSender, args: Array[String]) = if(args.length == 1) getUsernameOptions(args) else null
}
