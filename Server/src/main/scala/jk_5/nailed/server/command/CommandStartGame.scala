package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map

/**
 * No description given
 *
 * @author jk-5
 */
object CommandStartGame extends ScalaCommand {

  val name = "startgame"
  val usage = "/startgame - Starts the game in your current map"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    map.getGameManager.startGame()
  }
}
