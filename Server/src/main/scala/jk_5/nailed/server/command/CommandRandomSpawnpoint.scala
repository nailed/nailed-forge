package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.teleport.TeleportOptions
import jk_5.nailed.api.NailedAPI

/**
 * No description given
 *
 * @author jk-5
 */
object CommandRandomSpawnpoint extends ScalaCommand {

  val name = "randomspawnpoint"
  val usage = "/randomspawnpoint [player] - Teleports you or the specified player to a random spawnpoint"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]) =
    if(args.length == 0){
      val options = new TeleportOptions()
      options.setLocation(map.getRandomSpawnpoint)
      NailedAPI.getTeleporter.teleportEntity(sender.getEntity, options)
    }else{
      this.processCommandWithMap(sender.getEntity, map, args)
    }

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]) =
    if(args.length == 1){
      val players = getPlayersList(sender, args(0))
      for(player <- players){
        val options = new TeleportOptions
        options.setLocation(map.getRandomSpawnpoint)
        NailedAPI.getTeleporter.teleportEntity(player, options)
      }
    }else throw new WrongUsageException("Usage: /randomspawnpoint [player]")
}
