package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.ChatComponentTranslation

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSeed extends ScalaCommand {

  override val name = "seed"
  override val usage = "/seed - Shows the seed of the world you're currently in"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    sender.addChatMessage(new ChatComponentTranslation("commands.seed.success", map.getWorld.getSeed))
  }
}
