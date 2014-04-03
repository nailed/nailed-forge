package jk_5.nailed.mumblelink

import cpw.mods.fml.common.{FMLCommonHandler, Mod}
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.util.ChatComponentText
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "NailedMumble", name = "NailedMumble", version = "0.1", dependencies = "required-after:Nailed", modLanguage = "scala")
object MumbleLink {

  private final val notificationDelay = 100l
  private var messagePrinted = false
  private var start = -1l

  @Mod.EventHandler def preInit(event: FMLPreInitializationEvent){
    Mumble.tryInit()
    FMLCommonHandler.instance.bus.register(this)
  }

  @SubscribeEvent def onRender(event: TickEvent.RenderTickEvent): Unit = event.phase match {
    case TickEvent.Phase.START =>
      if(!Mumble.inited && !Mumble.tryInit) return
      this.update()
      if(Mumble.inited){
        if(mc != null && mc.theWorld != null){
          if(!this.messagePrinted){
            val now = mc.theWorld.getWorldTime
            if(this.start == -1){
              this.start = now
            }
            if(start + notificationDelay < now){
              this.messagePrinted = true
              mc.thePlayer.addChatMessage(new ChatComponentText("Linked to mumble"))
            }
          }
        }else this.messagePrinted = false
      }
    case _ =>
  }

  def update() = try{
    val fAvatarFrontX = 1f
    val fAvatarFrontY = 0f
    val fAvatarFrontZ = 1f
    val fCameraFrontX = 1f
    val fCameraFrontY = 0f
    val fCameraFrontZ = 1f
    val fAvatarTopX = 0f
    val fAvatarTopY = 1f
    val fAvatarTopZ = 0f
    val fCameraTopX = 0f
    val fCameraTopY = 1f
    val fCameraTopZ = 0f
    val camera = mc.thePlayer.getLookVec
    val fAvatarPosition = Array(mc.thePlayer.posX.toFloat, mc.thePlayer.posZ.toFloat, mc.thePlayer.posY.toFloat)
    val fAvatarFront = Array(camera.xCoord.toFloat * fAvatarFrontX, camera.zCoord.toFloat * fAvatarFrontZ, camera.yCoord.toFloat * fAvatarFrontY)
    val fAvatarTop = Array(fAvatarTopX, fAvatarTopZ, fAvatarTopY)
    val fCameraPosition = Array(mc.thePlayer.posX.toFloat, mc.thePlayer.posZ.toFloat, mc.thePlayer.posY.toFloat)
    val fCameraFront = Array(camera.xCoord.toFloat * fCameraFrontX, camera.zCoord.toFloat * fCameraFrontZ, camera.yCoord.toFloat * fCameraFrontY)
    val fCameraTop = Array(fCameraTopX, fCameraTopZ, fCameraTopY)
    val identity = mc.thePlayer.getGameProfile.getName
    val context = "NailedGlobal"
    val name = "Nailed"
    val description = "Nailed mumble plugin"
    Mumble.update(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context)
  }catch{
    case ignored: Exception =>
  }
}
