package jk_5.nailed.client

import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import net.minecraft.util.MathHelper
import jk_5.nailed.client.blocks.NailedBlocks
import jk_5.nailed.client.network.ClientNetworkHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import jk_5.nailed.network.NailedPacket

/**
 * No description given
 *
 * @author jk-5
 */
object TickHandlerClient {

  private var wasJumping = false
  private var wasSneaking = false
  private var ellapsedTicks = 0
  private var totalTicks = 0l
  private final val sendFpsInterval = 40
  private var fpsCounter = 0
  private var fps = 0
  private var fpsUpdateTime = Minecraft.getSystemTime

  @SubscribeEvent def onClientTick(event: TickEvent.ClientTickEvent){
    if(event.phase eq TickEvent.Phase.START) return
    val player = mc.thePlayer
    val world = mc.theWorld
    if(player != null && world != null){
      val x = MathHelper.floor_double(player.posX)
      val y = MathHelper.floor_double(player.boundingBox.minY) - 1
      val z = MathHelper.floor_double(player.posZ)
      val block = world.getBlock(x, y, z)
      val meta = world.getBlockMetadata(x, y, z)
      if(block == NailedBlocks.stat && meta == 2){
        if(player.movementInput.jump && !wasJumping){
          ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(x, y, z, 0.asInstanceOf[Byte]))
        }
        if(player.movementInput.sneak && !wasSneaking){
          ClientNetworkHandler.sendPacketToServer(new NailedPacket.MovementEvent(x, y, z, 1.asInstanceOf[Byte]))
        }
      }
      wasJumping = player.movementInput.jump
      wasSneaking = player.movementInput.sneak
    }
    if(this.sendFpsInterval > 0){
      if(this.ellapsedTicks >= this.sendFpsInterval){
        ClientNetworkHandler.sendPacketToServer(new NailedPacket.FPSSummary(this.fps))
        this.ellapsedTicks = -1
      }
      this.ellapsedTicks += 1
    }
    totalTicks += 1
  }

  @SubscribeEvent def onRender(event: TickEvent.RenderTickEvent) = if(event.phase == TickEvent.Phase.END){
    fpsCounter += 1
    while(Minecraft.getSystemTime >= this.fpsUpdateTime + 1000){
      this.fps = this.fpsCounter
      this.fpsCounter = 0
      this.fpsUpdateTime += 1000
    }
  }

  def blinkOn = totalTicks / 6 % 2 == 0
}
