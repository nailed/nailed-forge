package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import scala.collection.mutable
import jk_5.nailed.api.map.teleport.TeleportOptions
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.map.Location

/**
 * No description given
 *
 * @author jk-5
 */
object CommandTp extends ScalaCommand {

  val name = "tp"
  val usage = "/tp <teleportingPlayer> [targetPlayer|x y z] - Teleport a player to another player or coordinates"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    val locations = mutable.ArrayBuffer[(Entity, TeleportOptions)]()
    args.length match {
      case 1 => //  /tp destination
        val teleporting = sender match {
          case p: EntityPlayer => p
          case _ => throw new CommandException("We can't teleport you as you aren't a player")
        }
        val target = Option(NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0)))
        if(target.isEmpty) throw new CommandException(s"Player ${args(0)} was not found")
        val option = TeleportOptions.builder().location(target.get.getLocation).destination(target.get.getCurrentMap).build
        locations += ((teleporting, option))
      case 2 => //  /tp teleportingPlayer destinationPlayer | destinationMap
        val option = getDestination(sender, args(1))
        getPlayersList(sender, args(0)).foreach(p => locations += ((p, option)))
      case 3 => //  /tp x y z
        val teleporting = sender match {
          case p: EntityPlayer => p
          case _ => throw new CommandException("We can't teleport you as you aren't a player")
        }
        val x = handleRelativeNumber(sender, teleporting.posX, args(0))
        val y = handleRelativeNumber(sender, teleporting.posY, args(1), 0, 0)
        val z = handleRelativeNumber(sender, teleporting.posZ, args(2))
        val option = TeleportOptions.builder().location(new Location(x, y, z, teleporting.rotationYaw, teleporting.rotationPitch)).destination(NailedAPI.getMapLoader.getMap(teleporting.worldObj)).build()
        locations += ((teleporting, option))
      case 4 => //  /tp player x y z
        getPlayersList(sender, args(0)).foreach(p => {
          val x = handleRelativeNumber(sender, p.posX, args(0))
          val y = handleRelativeNumber(sender, p.posY, args(1), 0, 0)
          val z = handleRelativeNumber(sender, p.posZ, args(2))
          val option = TeleportOptions.builder().location(new Location(x, y, z, p.rotationYaw, p.rotationPitch)).destination(NailedAPI.getMapLoader.getMap(p.worldObj)).build()
          locations += ((p, option))
        })
      case 5 => //  /tp player x y z options
        val opt = args(4)
        val particles = !opt.contains("noparticle")
        val sound = !opt.contains("nosound")
        val momentum = opt.contains("momentum")
        val clearInv = opt.contains("clearinventory")
        getPlayersList(sender, args(0)).foreach(p => {
          val x = handleRelativeNumber(sender, p.posX, args(0))
          val y = handleRelativeNumber(sender, p.posY, args(1), 0, 0)
          val z = handleRelativeNumber(sender, p.posZ, args(2))
          val option = new TeleportOptions
          option.setSound(if(sound) option.getSound else null)
          option.setSpawnParticles(particles)
          option.setMaintainMomentum(momentum)
          option.setClearInventory(clearInv)
          option.setLocation(new Location(x, y, z, p.rotationYaw, p.rotationPitch))
          option.setDestination(NailedAPI.getMapLoader.getMap(p.worldObj))
          locations += ((p, option))
        })
      case _ => throw new WrongUsageException("/tp <teleportingPlayer> [targetPlayer|x y z]")
    }
    locations.foreach(p => NailedAPI.getTeleporter.teleportEntity(p._1, p._2))
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1 || args.length == 2) getUsernameOptions(args) else null

  private def getDestination(sender: ICommandSender, data: String): TeleportOptions = {
    val dest = new TeleportOptions
    Option(NailedAPI.getPlayerRegistry.getPlayerByUsername(data)) match {
      case Some(player) =>
        dest.setLocation(player.getLocation)
        dest.setDestination(player.getCurrentMap)
      case None =>
        Option(NailedAPI.getMapLoader.getMap(data)) match {
          case Some(map) =>
            if(map.getMappack != null) dest.setLocation(map.getMappack.getMappackMetadata.getSpawnPoint)
            dest.setDestination(map)
          case None => throw new CommandException("No map/player found matching that name")
        }
    }
    dest
  }
}
