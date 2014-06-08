package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map

/**
 * No description given
 *
 * @author jk-5
 */
object CommandEdit extends ScalaCommand {

  override val name = "edit"
  override val usage = "/edit - Toggle edit mode"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    sender.setEditModeEnabled(!sender.isEditModeEnabled)
  }
}
