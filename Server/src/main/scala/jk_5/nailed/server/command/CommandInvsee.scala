package jk_5.nailed.server.command

import jk_5.nailed.api.player.Player
import jk_5.nailed.api.map.Map
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.util.invsee.InventoryOtherPlayer
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.inventory.ContainerChest
import net.minecraft.command.ICommandSender

/**
 * No description given
 *
 * @author jk-5
 */
object CommandInvsee extends ScalaCommand {

  val name = "invsee"
  val usage = "/invsee <player> - Lets you look at and edit the inventory of another player"

  override def processCommandPlayer(sender: Player, map: Map, args: Array[String]){
    if(args.length != 1) throw new WrongUsageException("Usage: /invsee <player>")
    val player = NailedAPI.getPlayerRegistry.getPlayerByUsername(args(0))
    if(player == null || !player.isOnline) throw new CommandException("That player is not online!")
    val entity = sender.getEntity
    if(entity.openContainer != entity.inventoryContainer) entity.closeScreen()
    entity.getNextWindowId()

    val chest = new InventoryOtherPlayer(player.getEntity, entity)
    entity.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(entity.currentWindowId, 0, chest.getInventoryName, chest.getSizeInventory, true))
    entity.openContainer = new ContainerChest(entity.inventory, chest)
    entity.openContainer.windowId = entity.currentWindowId
    entity.openContainer.addCraftingToCrafters(entity)
  }

  override def addAutocomplete(sender: ICommandSender, args: Array[String]) = if(args.length == 1) getUsernameOptions(args) else null
}
