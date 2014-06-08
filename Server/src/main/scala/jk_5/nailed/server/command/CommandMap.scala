package jk_5.nailed.server.command

import net.minecraftforge.permissions.api.{PermissionsManager, RegisteredPermValue}
import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.util.ScalaCallback._
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.ipc.mappack.IpcMappackRegistry
import jk_5.nailed.ipc.IpcManager
import jk_5.nailed.ipc.packet.PacketRequestMappackLoad
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}
import net.minecraft.event.{ClickEvent, HoverEvent}
import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
object CommandMap extends ScalaCommand with SubpermissionCommand {

  val name = "map"
  val usage = "/map <create|remove|list> - Create or remove maps, or list all currently loaded maps"

  private var createPerm: String = null
  private var removePerm: String = null
  private var listPerm: String = null

  override def registerPermissions(owner: String){
    PermissionsManager.registerPermission(createPerm = owner + ".commands.map.create", RegisteredPermValue.OP)
    PermissionsManager.registerPermission(removePerm = owner + ".commands.map.remove", RegisteredPermValue.OP)
    PermissionsManager.registerPermission(listPerm = owner + ".commands.map.list", RegisteredPermValue.TRUE)
  }

  override def hasPermission(sender: String, args: Array[String]) =
    if(args.length == 0) true
    else if("create".equalsIgnoreCase(args(0))){
      PermissionsManager.getPerm(sender, createPerm).check()
    }else if("remove".equalsIgnoreCase(args(0))){
      PermissionsManager.getPerm(sender, removePerm).check()
    }else if("list".equalsIgnoreCase(args(0))){
      PermissionsManager.getPerm(sender, listPerm).check()
    }else true

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length == 0) throw new WrongUsageException("/map <create:remove:list>")
    if(args(0).equalsIgnoreCase("create")){
      if(args.length == 1) throw new WrongUsageException("/map create <mappackName>")
      val name = args(1)
      val mappack = NailedAPI.getMappackLoader.getMappack(name)
      if(mappack == null){
        if(IpcMappackRegistry.getRemoteMappacks.contains("name")){
          IpcManager.instance().sendPacket(new PacketRequestMappackLoad(name))

          val msg = new ChatComponentText("Sent load request to IPC server")
          msg.getChatStyle.setColor(EnumChatFormatting.GREEN)
          sender.addChatMessage(msg)
        }else throw new CommandException("Mappack does not exist")
      }else{
        val component = new ChatComponentText("Loading " + mappack.getMappackID)
        component.getChatStyle.setColor(EnumChatFormatting.GREEN)
        sender.addChatMessage(component)
        NailedAPI.getMapLoader.createMapServer(mappack, wrapCallback(m => {
          val component = new ChatComponentText("Loaded ")
          component.getChatStyle.setColor(EnumChatFormatting.GREEN)
          val comp = new ChatComponentText(m.getSaveFileName)
          comp.getChatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Teleport to the map")))
          comp.getChatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + m.getSaveFileName))
          component.appendSibling(comp)
          sender.addChatMessage(component)
        }))
      }
    }else if(args(0).equalsIgnoreCase("remove")){
      if(args.length == 1) throw new WrongUsageException("/map remove <mapid>")
      val map = NailedAPI.getMapLoader.getMap(args(1))
      if(map == null) throw new CommandException("Map does not exist")
      map.unloadAndRemove()

      val component = new ChatComponentText("Removed " + map.getSaveFileName)
      component.getChatStyle.setColor(EnumChatFormatting.GREEN)
      sender.addChatMessage(component)
    }else if(args(0).equalsIgnoreCase("list")){
      val base = new ChatComponentText("")
      val c = new ChatComponentText("Loaded maps: ")
      c.getChatStyle.setColor(EnumChatFormatting.GREEN)
      base.getChatStyle.setColor(EnumChatFormatting.GRAY)
      base.appendSibling(c)
      var first = true
      for(map <- NailedAPI.getMapLoader.getMaps){
        if(!first) base.appendText(", ")
        val comp = new ChatComponentText(map.getSaveFileName)
        comp.getChatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to teleport")))
        comp.getChatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + map.getSaveFileName))
        base.appendSibling(comp)
        first = false
      }
      sender.addChatMessage(base)
    }else throw new WrongUsageException("/map <create:remove:list>")
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]): List[String] =
    if(args.length == 1) getOptions(args, "create", "remove", "list")
    else if(args.length == 2){
      if(args(0).equalsIgnoreCase("create")){
        val ret = mutable.ListBuffer[String]()
        ret ++= NailedAPI.getMappackLoader.getMappacks.map(_.getMappackID)
        ret ++= IpcMappackRegistry.getRemoteMappacks
        getOptions(args, ret)
      }else if(args(0).equalsIgnoreCase("remove")){
        getOptions(args, NailedAPI.getMapLoader.getMaps.map(_.getSaveFileName))
      }else null
    }else null
}
