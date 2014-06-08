package jk_5.nailed.server.command

import net.minecraft.command.{ICommand, ICommandSender}
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.NailedAPI
import net.minecraft.server.MinecraftServer
import scala.collection.JavaConverters._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSudo extends ScalaCommand {

  val name = "sudo"
  val usage = "/sudo <player> <command> - Execute a command as another player"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) = Option(NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))) match {
    case Some(player) =>
      val newArray = new Array[String](args.length - 1)
      System.arraycopy(args, 1, newArray, 0, newArray.length)
      MinecraftServer.getServer.getCommandManager.executeCommand(player.getEntity, newArray.mkString(" "))
    case None => throw new CommandException("Unknown player")
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] = args.length match {
    case 1 => getUsernameOptions(args)
    case 2 =>
      val target = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
      getOptions(args, MinecraftServer.getServer.getCommandManager.getPossibleCommands(target.getEntity).asInstanceOf[java.util.List[String]] asScala)
    case l if l > 2 =>
      val target = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
      val cmd = MinecraftServer.getServer.getCommandManager.getCommands.get(args(1)).asInstanceOf[ICommand]
      if(cmd == null) null
      else{
        val newArgs = new Array[String](args.length - 2)
        System.arraycopy(args, 2, newArgs, 0, args.length - 2)
        cmd.addTabCompletionOptions(target.getEntity, newArgs).asInstanceOf[java.util.List[String]].asScala.toList
      }
    case _ => null
  }
}
