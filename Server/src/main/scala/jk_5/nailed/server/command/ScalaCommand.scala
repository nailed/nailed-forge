package jk_5.nailed.server.command

import net.minecraft.command.{CommandException, ICommandSender}
import java.util
import scala.collection.JavaConverters._
import jk_5.nailed.api.NailedAPI
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map

/**
 * No description given
 *
 * @author jk-5
 */
abstract class ScalaCommand extends ComparedCommand {

  val name: String
  val usage: String
  def aliases: Array[String] = Array.empty

  final override def getCommandName = this.name
  final override def getCommandUsage(sender: ICommandSender) = this.usage
  final override def getCommandAliases = util.Arrays.asList(this.aliases)
  final override def canCommandSenderUseCommand(sender: ICommandSender) = true
  final override def addTabCompletionOptions(sender: ICommandSender, args: Array[String]): util.List[_] = this.addAutocomplete(sender, args).asJava
  final override def isUsernameIndex(args: Array[String], index: Int) = false
  final override def processCommand(sender: ICommandSender, args: Array[String]){
    val map = NailedAPI.getMapLoader.getMap(sender.getEntityWorld)
    sender match {
      case p: EntityPlayer => this.processCommandPlayer(NailedAPI.getPlayerRegistry.getPlayer(p), map, args)
      case _ => this.processCommandWithMap(sender, map, args)
    }
  }

  def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] = null
  def processCommandPlayer(sender: Player, map: Map, args: Array[String]): Unit = this.processCommandWithMap(sender.getEntity, map, args)
  def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]): Unit = throw new CommandException("commands.nailed.error.notValid")
}
