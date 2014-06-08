package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.effect.FireworkRandomizer

/**
 * No description given
 *
 * @author jk-5
 */
object CommandFirework extends ScalaCommand {

  override val name = "firework"
  override val usage = "/firework - Spawns a random firework rocket"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val coords = sender.getPlayerCoordinates
    FireworkRandomizer.getRandomEffect.toFirework.spawnInWorld(map, coords.posX, coords.posY, coords.posZ)
  }
}
