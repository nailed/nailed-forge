package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import net.minecraft.util.ChatComponentText
import jk_5.nailed.api.NailedAPI
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandFps extends ScalaCommand {

  val name = "fps"
  val usage = "/fps - Displays the FPS of all online players"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) =
    if(args.length > 0){
      val player = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
      sender.addChatMessage(new ChatComponentText(player.getChatPrefix + ": " + player.getFps + " FPS"))
    }else{
      var total, count = 0
      for(player <- NailedAPI.getPlayerRegistry.getOnlinePlayers){
        sender.addChatMessage(new ChatComponentText(player.getChatPrefix))
        count += 1
        total += player.getFps
      }
      sender.addChatMessage(new ChatComponentText("Average: " + (total / count) + " FPS"));
    }
}
