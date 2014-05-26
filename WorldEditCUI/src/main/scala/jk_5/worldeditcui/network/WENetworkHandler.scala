package jk_5.worldeditcui.network

import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry}
import io.netty.channel._
import io.netty.channel.ChannelHandler.Sharable
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import cpw.mods.fml.relauncher.Side
import jk_5.worldeditcui.network.packet._
import scala.collection.immutable

/**
 * No description given
 *
 * @author jk-5
 */
object WENetworkHandler {

  lazy val channels = NetworkRegistry.INSTANCE.newChannel("WECUI", new StringEncoder, new StringDecoder, WEMessageHandler)

  def load(){
    channels.get(Side.CLIENT) //Force the channels to be initialized
  }

  def sendMessage(msg: String){
    val channel = channels.get(Side.CLIENT)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
    channel.writeOutbound(msg)
  }
}

@Sharable
object WEMessageHandler extends ChannelDuplexHandler {

  final val packets = immutable.HashMap(
    "s" -> classOf[PacketSelection],
    "p" -> classOf[PacketPoint3D],
    "p2" -> classOf[PacketPoint2D],
    "e" -> classOf[PacketEllipsoid],
    "cy" -> classOf[PacketCylinder],
    "mm" -> classOf[PacketMinMax],
    "u" -> classOf[PacketUpdate]
  )

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = msg match {
    case m: String =>
      val data = m.split("[|]", 2)
      val packet = packets.find(_._1 == data(0))
      packet.foreach(_._2.newInstance().processPacket(data(1).split("[|]")))
    case _ =>
  }
}
