package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import java.util
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.collection.mutable
import jk_5.nailed.api.NailedAPI
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import net.minecraft.server.MinecraftServer

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

  type CommandException = net.minecraft.command.CommandException
  type WrongUsageException = net.minecraft.command.WrongUsageException
  type CommandNotFoundException = net.minecraft.command.CommandNotFoundException
  type NumberInvalidException = net.minecraft.command.NumberInvalidException
  type PlayerNotFoundException = net.minecraft.command.PlayerNotFoundException
  type SyntaxErrorException = net.minecraft.command.SyntaxErrorException

  def getOptions(args: Array[String], options: String*): List[String] = {
    val last = args(args.length - 1)
    List(options.filter(_.regionMatches(true, 0, last, 0, last.length)): _*)
  }

  def getOptions(args: Array[String], options: Iterable[String]): List[String] = {
    val last = args(args.length - 1)
    val ret = mutable.ListBuffer[String]()
    options.filter(_.regionMatches(true, 0, last, 0, last.length)).foreach(o => ret += o)
    ret.toList
  }

  @inline def getUsernameOptions(args: Array[String]): List[String] = getOptions(args, MinecraftServer.getServer.getAllUsernames)
  @inline def getUsernameOptions(args: Array[String], map: Map): List[String] = getOptions(args, map.getPlayers.map(_.getUsername))
}
