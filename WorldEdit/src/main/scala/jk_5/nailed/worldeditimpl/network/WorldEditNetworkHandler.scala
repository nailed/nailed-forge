package jk_5.nailed.worldeditimpl.network

import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.util.CharsetUtil
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.worldeditimpl.NailedWorldEdit
import cpw.mods.fml.common.network.handshake.NetworkDispatcher
import net.minecraft.network.NetHandlerPlayServer
import io.netty.channel.ChannelHandler.Sharable

/**
 * No description given
 *
 * @author jk-5
 */
object WorldEditNetworkHandler {

  lazy val channels = NetworkRegistry.INSTANCE.newChannel("WECUI", new StringEncoder(CharsetUtil.UTF_8), new StringDecoder(CharsetUtil.UTF_8), WECUIHandler)

  def load(){
    channels.get(Side.SERVER) //Populate the channels
  }

  def sendMessageToPlayer(player: EntityPlayer, message: String){
    val channel = channels.get(Side.SERVER)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
    channel.writeOutbound(message)
  }
}

@Sharable
object WECUIHandler extends SimpleChannelInboundHandler[String] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: String){
    val player = ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get().manager.getNetHandler.asInstanceOf[NetHandlerPlayServer].playerEntity
    val session = NailedWorldEdit.getSession(player)
    if(session != null && !session.hasCUISupport){
      session.handleCUIInitializationMessage(msg)
    }
  }
}
