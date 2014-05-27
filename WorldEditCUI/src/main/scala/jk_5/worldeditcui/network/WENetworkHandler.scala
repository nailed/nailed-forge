package jk_5.worldeditcui.network

import cpw.mods.fml.common.network.{FMLEmbeddedChannel, FMLOutboundHandler, NetworkRegistry}
import io.netty.channel._
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.codec.string.StringEncoder
import cpw.mods.fml.relauncher.Side
import jk_5.worldeditcui.network.packet._
import scala.collection.immutable
import io.netty.util.CharsetUtil
import io.netty.handler.codec.{MessageToMessageCodec, MessageToMessageDecoder}
import cpw.mods.fml.common.network.internal.FMLProxyPacket
import java.util
import io.netty.buffer.{Unpooled, ByteBuf}
import cpw.mods.fml.common.network.handshake.NetworkDispatcher

/**
 * No description given
 *
 * @author jk-5
 */
object WENetworkHandler {

  var channel: FMLEmbeddedChannel = _

  def load(){
    channel = NetworkRegistry.INSTANCE.newChannel("WECUI", WECodec, WEMessageHandler).get(Side.CLIENT)
  }

  def sendMessage(msg: String){
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
    channel.writeOutbound(msg)
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
object WEMessageHandler extends SimpleChannelInboundHandler[String] {

  final val packets = immutable.HashMap(
    "s" -> classOf[PacketSelection],
    "p" -> classOf[PacketPoint3D],
    "p2" -> classOf[PacketPoint2D],
    "e" -> classOf[PacketEllipsoid],
    "cy" -> classOf[PacketCylinder],
    "mm" -> classOf[PacketMinMax],
    "u" -> classOf[PacketUpdate]
  )

  override def channelRead0(ctx: ChannelHandlerContext, m: String){
    val data = m.trim.split("[|]", 2)
    val packet = packets.find(_._1 == data(0))
    packet.foreach(_._2.newInstance().processPacket(data(1).split("[|]")))
  }
}
