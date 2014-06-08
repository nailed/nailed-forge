package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object CommandZone extends ScalaCommand {

  val name = "zone"
  val usage = "/zone - Displays the zone you are currently in"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    val zones = map.getZoneManager.getZones(sender)
    sender.sendChat(zones.mkString(", "))
  }
}
