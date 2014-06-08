package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map

/**
 * No description given
 *
 * @author jk-5
 */
object CommandLobby extends ScalaCommand {

  val name = "lobby"
  val usage = "/lobby - Teleports you to the lobby"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    sender.teleportToLobby()
  }
}
