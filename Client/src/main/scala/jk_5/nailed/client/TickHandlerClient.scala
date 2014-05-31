package jk_5.nailed.client

import cpw.mods.fml.common.gameevent.TickEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import jk_5.nailed.client.network.ClientNetworkHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import jk_5.nailed.network.NailedPacket

/**
 * No description given
 *
 * @author jk-5
 */
object TickHandlerClient {

  private var ellapsedTicks = 0
  private var totalTicks = 0l
  private final val sendFpsInterval = 40
  private var fpsCounter = 0
  private var fps = 0
  private var fpsUpdateTime = Minecraft.getSystemTime

  @SubscribeEvent def onClientTick(event: TickEvent.ClientTickEvent) = if(event.phase == TickEvent.Phase.END){
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
