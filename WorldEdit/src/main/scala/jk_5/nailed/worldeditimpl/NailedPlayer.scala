package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit.{LocalWorld, WorldVector, LocalPlayer, Vector}
import scala.ref.WeakReference
import net.minecraft.entity.player.EntityPlayerMP
import com.sk89q.worldedit.bags.BlockBag
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.util.{ChatComponentText, EnumChatFormatting}
import com.sk89q.worldedit.cui.CUIEvent

/**
 * No description given
 *
 * @author jk-5
 */
object NailedPlayer {
  def apply(server: NailedServerInterface, player: EntityPlayerMP) = new NailedPlayer(server, new WeakReference[EntityPlayerMP](player))
}
class NailedPlayer private (val server: NailedServerInterface, val player: WeakReference[EntityPlayerMP]) extends LocalPlayer(server) {

  final val CHATPREFIX = ""
  val playerName: String = player.get.get.getCommandSenderName

  def getPlayer: EntityPlayerMP = player.get.getOrElse(null)
  override def getName = this.playerName
  override def getGroups = Array[String]()
  override def getInventoryBlockBag: BlockBag = null

  def getItemInHand: Int = this.player.get match {
    case Some(thePlayer) =>
      val stack = thePlayer.getCurrentEquippedItem
      if(stack != null){
        val item = stack.getItem
        if(item != null){
          return Item.getIdFromItem(item)
        }
      }
      0
    case _ => 0
  }

  def getPitch: Double = this.player.get match {
    case Some(thePlayer) => thePlayer.rotationPitch
    case _ => 0.0
  }

  def getYaw: Double = this.player.get match {
    case Some(thePlayer) => thePlayer.rotationYaw
    case _ => 0.0
  }

  def getPosition: WorldVector = this.player.get match {
    case Some(thePlayer) => new WorldVector(NailedWorld(thePlayer.worldObj), thePlayer.posX, thePlayer.posY, thePlayer.posZ)
    case _ => null
  }

  def setPosition(pos: Vector, pitch: Float, yaw: Float) = this.player.get match {
    case Some(thePlayer) => thePlayer.playerNetServerHandler.setPlayerLocation(pos.getX, pos.getY, pos.getZ, yaw, pitch)
    case _ =>
  }

  def getWorld: LocalWorld = this.player.get match {
    case Some(thePlayer) => NailedWorld(thePlayer.worldObj)
    case _ => null
  }

  def giveItem(typ: Int, amt: Int) = this.player.get match {
    case Some(thePlayer) =>
      val item = Item.getItemById(typ)
      if(item != null){
        val stack = new ItemStack(item, amt, 0)
        thePlayer.inventory.addItemStackToInventory(stack)
      }
    case _ =>
  }

  def hasPermission(perm: String) = this.player.get.isDefined //TODO

  def print(msg: String) = msg.split("\n").foreach(l => this.sendMessage(CHATPREFIX + EnumChatFormatting.LIGHT_PURPLE + l))
  def printDebug(msg: String) = msg.split("\n").foreach(l => this.sendMessage(CHATPREFIX + EnumChatFormatting.GRAY + l))
  def printError(msg: String) = msg.split("\n").foreach(l => this.sendMessage(CHATPREFIX + EnumChatFormatting.RED + l))
  def printRaw(msg: String) = msg.split("\n").foreach(l => this.sendMessage(CHATPREFIX + l))

  def sendMessage(msg: String) = this.player.get match {
    case Some(thePlayer) => thePlayer.addChatMessage(new ChatComponentText(msg))
    case _ =>
  }

  override def dispatchCUIEvent(event: CUIEvent){
    this.server.asInstanceOf[NailedServerInterface].dispatchCUIEvent(this, event)
  }
}
