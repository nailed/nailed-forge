package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.util.NailedFoodStats

/**
 * No description given
 *
 * @author jk-5
 */
object CommandFeed extends ScalaCommand {

  override val name = "feed"
  override val usage = "/feed [player] - Feeds yourself or the player specified"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) =
    if(args.length == 0) sender match {
      case _: EntityPlayer if map.getGameManager.isGameRunning => throw new CommandException("You may not feed people while a game is running")
      case p: EntityPlayer => p.getFoodStats.asInstanceOf[NailedFoodStats].setFood(20)
      case _ => throw new WrongUsageException("Usage: /feed <player>")
    }else if(args.length > 1){
      NailedCommand.getPlayersList(sender, args(0)).foreach(_.getFoodStats.asInstanceOf[NailedFoodStats].setFood(20))
    }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]) = if(args.length == 1) getUsernameOptions(args) else null
}
