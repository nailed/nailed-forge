package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.util.ScalaCallback._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandSafehouse extends ScalaCommand {

  val name = "safehouse"
  val usage = "/safehouse - Teleports you to your safehouse"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    val mappack = NailedAPI.getMappackLoader.getMappack("safehouse")
    if(mappack == null){
      logger.warn("No safehouse mappack was found. Not teleporting {}", sender.getUsername)
      throw new CommandException("No safehouse mappack was found")
    }
    NailedAPI.getMapLoader.createMapServer(mappack, sender.teleportToMap(_))
  }
}
