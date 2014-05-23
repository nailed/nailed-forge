package jk_5.nailed.worldeditimpl

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLServerStartingEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.network.NetworkCheckHandler
import cpw.mods.fml.relauncher.Side
import jk_5.nailed.worldeditimpl.network.WorldEditNetworkHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.CommandEvent
import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import scala.collection.mutable
import net.minecraft.command.CommandException
import com.sk89q.worldedit.{WorldVector, WorldEdit}
import java.io.File
import net.minecraftforge.event.entity.player.PlayerInteractEvent

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "NailedWorldEdit", name = "Nailed WorldEdit", version = "0.1-SNAPSHOT", modLanguage = "scala")
object NailedWorldEdit {

  @NetworkCheckHandler def accept(versions: java.util.Map[String, String], side: Side) = true

  var worldEdit: WorldEdit = _
  var workingDir: File = _
  var config: NailedWorldEditConfig = _
  var server: NailedServerInterface = _

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    this.workingDir = new File(event.getModConfigurationDirectory, "WorldEdit")
    this.workingDir.mkdir()

    new File(this.workingDir, "craftscripts").mkdir()
    new File(this.workingDir, "schematics").mkdir()

    this.config = new NailedWorldEditConfig(this.workingDir)

    WorldEditNetworkHandler.load()

    FMLCommonHandler.instance().bus().register(this)
    MinecraftForge.EVENT_BUS.register(this)
  }

  @EventHandler def serverStarting(event: FMLServerStartingEvent){
    this.server = new NailedServerInterface(event.getServer)
    this.worldEdit = new WorldEdit(this.server, this.config)
  }

  @SubscribeEvent def onCommand(event: CommandEvent): Unit = event.command match {
    case c: WorldEditCommand =>
      event.sender match {
        case p: EntityPlayer =>
          val builder = mutable.ArrayBuffer[String]()
          builder += "/" + event.command.getCommandName
          builder ++= event.parameters
          this.worldEdit.handleCommand(this.server.getLocalPlayer(event.sender.asInstanceOf[EntityPlayer]), builder.toArray)
        case _ => event.exception = new CommandException("This command is only usable by players")
      }
      event.setCanceled(true)
    case _ =>
  }

  @SubscribeEvent def onTick(event: TickEvent.ServerTickEvent){
    if(event.phase == TickEvent.Phase.START){
      if(this.server != null) this.server.onTick()
    }
  }

  @SubscribeEvent def onPlayerInteract(event: PlayerInteractEvent){
    if(this.worldEdit == null || this.server == null) return
    val player = this.server.getLocalPlayer(event.entityPlayer)
    val world = VanillaWorld.getLocalWorld(event.entityPlayer.worldObj)
    val vec = new WorldVector(world, event.x, event.y, event.z)

    event.action match {
      case PlayerInteractEvent.Action.LEFT_CLICK_BLOCK =>
        if(this.worldEdit.handleBlockLeftClick(player, vec)) event.setCanceled(true)
        if(this.worldEdit.handleArmSwing(player)) event.setCanceled(true)
      case PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK =>
        if(this.worldEdit.handleBlockRightClick(player, vec)) event.setCanceled(true)
        if(this.worldEdit.handleRightClick(player)) event.setCanceled(true)
      case PlayerInteractEvent.Action.RIGHT_CLICK_AIR =>
        if(this.worldEdit.handleRightClick(player)) event.setCanceled(true)
      /*case PlayerInteractEvent.Action.RIGHT_CLICK_AIR =>
        if(this.worldEdit.handleArmSwing(player)) event.setCanceled(true)*/ //TODO
    }
  }

  def getSession(player: EntityPlayerMP) = if(this.server != null) this.worldEdit.getSession(this.server.getLocalPlayer(player)) else null
}
