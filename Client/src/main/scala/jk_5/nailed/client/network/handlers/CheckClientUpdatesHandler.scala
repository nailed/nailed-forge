package jk_5.nailed.client.network.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.network.NailedPacket
import jk_5.nailed.network.NailedPacket.CheckClientUpdates
import jk_5.nailed.client.updater.UpdaterApi
import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import net.minecraft.util.{EnumChatFormatting, ChatComponentText}

/**
 * No description given
 *
 * @author jk-5
 */
object CheckClientUpdatesHandler extends SimpleChannelInboundHandler[NailedPacket.CheckClientUpdates] {
  val updaterFactory = new ThreadFactoryBuilder().setDaemon(false).setNameFormat("Updater main thread %d").build()

  override def channelRead0(ctx: ChannelHandlerContext, msg: CheckClientUpdates){
    updaterFactory.newThread(new Runnable {
      override def run(){
        if(UpdaterApi.update()){
          val comp = new ChatComponentText("Installed a few updates. Restart the game to make them active")
          comp.getChatStyle.setColor(EnumChatFormatting.GREEN)
          mc.ingameGUI.getChatGUI.printChatMessage(comp)
        }
      }
    })
  }
}
