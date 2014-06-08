package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import jk_5.nailed.map.NailedMap
import jk_5.nailed.network.{NailedPacket, NailedNetworkHandler}

/**
 * No description given
 *
 * @author jk-5
 */
object CommandTerminal extends ScalaCommand {

  val name = "terminal"
  val usage = "/terminal - Displays a terminal"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    val nm = map.asInstanceOf[NailedMap]
    val machine = nm.getMachine
    NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.OpenTerminalGui(machine.getInstanceId, machine.getTerminal.getWidth, machine.getTerminal.getHeight), sender.getEntity)
    if(!machine.isOn){
      machine.turnOn()
      machine.terminalChanged = true
      nm.mounted = map.getMappack == null
      nm.mappackMount = null
    }
  }
}
