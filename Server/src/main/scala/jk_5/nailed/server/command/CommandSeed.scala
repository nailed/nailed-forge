package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.ChatComponentTranslation

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSeed extends NailedCommand("seed") {

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    sender.addChatMessage(new ChatComponentTranslation("commands.seed.success", map.getWorld.getSeed))
  }
}
