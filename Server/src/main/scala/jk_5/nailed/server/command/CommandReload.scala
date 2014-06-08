package jk_5.nailed.server.command

import net.minecraft.command.ICommandSender
import jk_5.nailed.api.map.Map
import jk_5.nailed.util.ScalaUtils._
import jk_5.nailed.util.ScalaCallback._
import net.minecraftforge.permissions.api.PermissionsManager
import jk_5.nailed.permissions.NailedPermissionFactory
import net.minecraft.util.{ChatComponentText, EnumChatFormatting}
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.ipc.IpcManager

/**
 * No description given
 *
 * @author jk-5
 */
object CommandReload extends ScalaCommand {

  val name = "reload"
  val usage = "/reload <mappacks|permissions|ipc> - Reloads the specified module"

  override def processCommandWithMap(sender: ICommandSender, map: Map, args: Array[String]){
    if(args.length != 1) throw new WrongUsageException("/reload <mappacks|permissions|ipc>")
    else{
      caseInsensitiveMatch(args(0)){
        case "permissions" =>
          PermissionsManager.getPermFactory match {
            case f: NailedPermissionFactory =>
              f.readConfig()
              val component = new ChatComponentText("Reloaded permissions!")
              component.getChatStyle.setColor(EnumChatFormatting.GREEN)
              sender.addChatMessage(component)
            case _ =>
          }
        case "mappacks" =>
          NailedAPI.getMappackLoader.loadMappacks(wrapCallback(l => {
            val comp = new ChatComponentText("Successfully loaded " + l.getMappacks.size() + " mappacks")
            comp.getChatStyle.setColor(EnumChatFormatting.GREEN)
            sender.addChatMessage(comp)
          }))
        case "ipc" =>
          NailedAPI.getScheduler.runTaskAsynchronously {
            case _ =>
              val future = IpcManager.instance().close()
              if(future != null) future.syncUninterruptibly()
              IpcManager.instance().start()
          }
        case _ => throw new WrongUsageException("/reload <mappacks|permissions|ipc>")
      }
    }
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]) = if(args.length == 1) getOptions(args, "permissions", "mappacks", "ipc") else null
}
