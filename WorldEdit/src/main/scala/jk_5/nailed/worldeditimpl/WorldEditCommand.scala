package jk_5.nailed.worldeditimpl

import net.minecraft.command.{ICommandSender, CommandBase}
import com.sk89q.minecraft.util.commands.Command
import net.minecraftforge.permissions.api.{RegisteredPermValue, PermissionsManager}
import java.util

/**
 * No description given
 *
 * @author jk-5
 */
class WorldEditCommand(command: Command) extends CommandBase {

  val commandName = command.aliases()(0)
  val usage = "/%s %s" format(this.commandName, command.usage())

  PermissionsManager.registerPermission("nailedworldedit.commands." + this.commandName, RegisteredPermValue.OP)

  override def getCommandAliases = util.Arrays.asList(this.command.aliases(): _*)
  override def getCommandName = this.commandName
  override def getCommandUsage(sender: ICommandSender) = this.usage
  override def processCommand(sender: ICommandSender, args: Array[String]) = {}
}
