package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit.{LocalPlayer, LocalWorld, ServerInterface}
import net.minecraft.server.MinecraftServer
import com.mumfrey.worldeditwrapper.impl.VanillaBiomeTypes
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.command.CommandHandler
import net.minecraft.entity.EntityList
import net.minecraft.item.Item
import jk_5.nailed.worldeditimpl.network.WorldEditNetworkHandler
import scala.collection.mutable
import scala.collection.JavaConversions._
import java.util
import com.sk89q.minecraft.util.commands.{CommandsManager, Command}
import com.sk89q.worldedit.cui.CUIEvent

/**
 * No description given
 *
 * @author jk-5
 */
class NailedServerInterface(val server: MinecraftServer) extends ServerInterface {

  final val players = mutable.HashMap[String, VanillaPlayer]()
  final val tasks = mutable.ArrayBuffer[WorldEditScheduledTask]()

  def getLocalPlayer(player: EntityPlayer): VanillaPlayer = {
    val playerName = player.getCommandSenderName
    var localPlayer = this.players.get(playerName)
    localPlayer match {
      case Some(p) => if(p.getPlayer != player) localPlayer = None
      case None =>
    }
    localPlayer match {
      case Some(p) =>
      case None =>
        localPlayer = Some(new VanillaPlayer(this, player.asInstanceOf[EntityPlayerMP]))
        this.players.put(playerName, localPlayer.get)
    }
    localPlayer.get
  }

  override def reload() = this.tasks.clear()
  override def getBiomes = VanillaBiomeTypes.getInstance
  override def isValidMobType(typ: String) = EntityList.stringToClassMapping.containsKey(typ)

  override def getWorlds: util.List[LocalWorld] = {
    val worlds = new util.ArrayList[LocalWorld](this.server.worldServers.length)
    for(world <- this.server.worldServers){
      worlds.add(VanillaWorld.getLocalWorld(world))
    }
    worlds
  }

  override def onCommandRegistration(commands: util.List[Command], manager: CommandsManager[LocalPlayer]){
    if(this.server != null){
      val serverCommandManager: CommandHandler = this.server.getCommandManager.asInstanceOf[CommandHandler]
      for (command <- commands) {
        serverCommandManager.registerCommand(new WorldEditCommand(command))
      }
    }
  }

  def resolveItem(name: String): Int = {
    if(name == null) return 0
    val item = Option(Item.itemRegistry.getObject(name).asInstanceOf[Item])
    item match {
      case Some(i) => Item.getIdFromItem(i)
      case _ =>
        Item.itemRegistry.map(_.asInstanceOf[Item]).foreach(item => {
          var iname = item.getUnlocalizedName
          if(iname != null){
            val dotPos = iname.indexOf('.')
            if(dotPos > 0) iname = iname.substring(dotPos)
            if(name.equalsIgnoreCase(iname)) return Item.getIdFromItem(item)
          }
        })
        0
    }
  }

  override def schedule(delay: Long, period: Long, task: Runnable): Int = {
    val scheduledTask: WorldEditScheduledTask = new WorldEditScheduledTask(task, period, delay)
    this.tasks.add(scheduledTask)
    this.tasks.size - 1
  }

  def onTick() = this.tasks.foreach(_.onTick())

  def dispatchCUIEvent(localPlayer: VanillaPlayer, event: CUIEvent){
    val message = this.packCUIEvent(event)
    WorldEditNetworkHandler.sendMessageToPlayer(localPlayer.getPlayer, message)
  }

  def packCUIEvent(event: CUIEvent): String = {
    val message = event.getTypeId
    val params = event.getParameters
    if(params.length > 0) message + "|" + params.mkString("|") else message
  }
}
