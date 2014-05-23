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

  def getItemInHand: Int = {
    val thePlayer = this.player.get
    if(thePlayer.isEmpty) return 0
    val stack = thePlayer.get.getCurrentEquippedItem
    if(stack != null){
      val item = stack.getItem
      if(item != null){
        return Item.getIdFromItem(item)
      }
    }
    0
  }

  def getPitch: Double = {
    val thePlayer = this.player.get
    if(thePlayer.isDefined) thePlayer.get.rotationPitch else 0.0
  }

  def getYaw: Double = {
    val thePlayer = this.player.get
    if(thePlayer.isDefined) thePlayer.get.rotationYaw else 0.0
  }

  def getPosition: WorldVector = {
    val thePlayer = this.player.get
    if(thePlayer.isDefined){
      new Nothing(VanillaWorld.getLocalWorld(thePlayer.get.worldObj), thePlayer.get.posX, thePlayer.get.posY, thePlayer.get.posZ)
    }else null
  }

  def setPosition(pos: Vector, pitch: Float, yaw: Float) {
    val thePlayer = this.player.get
    if(thePlayer.isDefined){
      thePlayer.get.playerNetServerHandler.setPlayerLocation(pos.getX, pos.getY, pos.getZ, yaw, pitch)
    }
  }

  def getWorld: LocalWorld = {
    val thePlayer = this.player.get
    if(thePlayer.isDefined){
      VanillaWorld.getLocalWorld(thePlayer.get.worldObj)
    }else null
  }

  def giveItem(typ: Int, amt: Int) {
    val thePlayer = this.player.get
    if(thePlayer.isDefined){
      val item = Item.getItemById(typ)
      if(item != null){
        val stack = new ItemStack(item, amt, 0)
        thePlayer.get.inventory.addItemStackToInventory(stack)
      }
    }
  }

  def hasPermission(perm: String) = this.player.get.isDefined //TODO

  def print(msg: String){
    for(line <- msg.split("\n")){
      this.sendMessage(CHATPREFIX + EnumChatFormatting.LIGHT_PURPLE + line)
    }
  }

  def printDebug(msg: String){
    for(line <- msg.split("\n")){
      this.sendMessage(CHATPREFIX + EnumChatFormatting.GRAY + line)
    }
  }

  def printError(msg: String){
    for(line <- msg.split("\n")){
      this.sendMessage(CHATPREFIX + EnumChatFormatting.RED + line)
    }
  }

  def printRaw(msg: String){
    for(line <- msg.split("\n")){
      this.sendMessage(CHATPREFIX + line)
    }
  }

  private def sendMessage(message: String){
    val thePlayer = this.player.get
    if(thePlayer.isDefined){
      thePlayer.get.addChatMessage(new ChatComponentText(message))
    }
  }

  override def dispatchCUIEvent(event: CUIEvent){
    this.server.asInstanceOf[NailedServerInterface].dispatchCUIEvent(this, event)
  }
}
