package jk_5.nailed.worldeditimpl.network

import cpw.mods.fml.common.network.{FMLEmbeddedChannel, FMLOutboundHandler, NetworkRegistry}
import io.netty.handler.codec.string.StringDecoder
import io.netty.util.CharsetUtil
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import jk_5.nailed.worldeditimpl.NailedWorldEdit
import cpw.mods.fml.common.network.handshake.NetworkDispatcher
import net.minecraft.network.NetHandlerPlayServer
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.codec.MessageToMessageCodec
import io.netty.buffer.{Unpooled, ByteBuf}
import java.util
import cpw.mods.fml.common.network.internal.FMLProxyPacket

/**
 * No description given
 *
 * @author jk-5
 */
object WorldEditNetworkHandler {

  var channel: FMLEmbeddedChannel = _

  def load(){
    channel = NetworkRegistry.INSTANCE.newChannel("WECUI", WECodec, WECUIHandler).get(Side.SERVER)
  }

  def sendMessageToPlayer(player: EntityPlayer, message: String){
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
    channel.writeOutbound(message)
  }
}

@Sharable
object WECodec extends MessageToMessageCodec[FMLProxyPacket, String] {
  override def encode(ctx: ChannelHandlerContext, msg: String, out: util.List[AnyRef]){
    val p = new FMLProxyPacket(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8), "WECUI")
    p.setDispatcher(ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get())
    out.add(p)
  }
  override def decode(ctx: ChannelHandlerContext, msg: FMLProxyPacket, out: util.List[AnyRef]){
    out.add(msg.payload().toString(CharsetUtil.UTF_8))
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
