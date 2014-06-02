package jk_5.nailed.network

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelInboundHandlerAdapter, ChannelHandlerContext, SimpleChannelInboundHandler}
import jk_5.nailed.network.NailedPacket._
import jk_5.nailed.ipc.{IpcEventListener, IpcManager}
import jk_5.nailed.ipc.packet.{PacketCheckAccount, PacketCreateAccount}
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.gui.IGuiReturnHandler
import java.util.regex.Pattern
import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry, NetworkHandshakeEstablished}
import cpw.mods.fml.relauncher.Side
import scala.collection.JavaConversions._
import net.minecraftforge.common.network.ForgeMessage

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object RegisterHandler extends SimpleChannelInboundHandler[NailedPacket.CreateAccount] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: CreateAccount){
    IpcManager.instance().sendPacket(new PacketCreateAccount(NailedAPI.getPlayerRegistry.getPlayer(NailedNetworkHandler.getPlayer(ctx)).getId(), msg.username, msg.email, msg.name, msg.password))
  }
}

@Sharable
object LoginHandler extends SimpleChannelInboundHandler[NailedPacket.Login] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: Login){
    IpcEventListener.loginPlayer(NailedNetworkHandler.getPlayer(ctx), msg.username, msg.password)
  }
}

@Sharable
object GuiReturnDataHandler extends SimpleChannelInboundHandler[NailedPacket.GuiReturnDataPacket] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: GuiReturnDataPacket){
    Option(NailedNetworkHandler.getPlayer(ctx).worldObj.getTileEntity(msg.x, msg.y, msg.z)) match {
      case Some(h: IGuiReturnHandler) => h.readGuiCloseData(msg.data)
      case _ =>
    }
  }
}

@Sharable
object FPSHandler extends SimpleChannelInboundHandler[NailedPacket.FPSSummary] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: FPSSummary){
    NailedAPI.getPlayerRegistry.getPlayer(NailedNetworkHandler.getPlayer(ctx)).setFps(msg.fps)
  }
}

@Sharable
object FieldStatusHandler extends SimpleChannelInboundHandler[NailedPacket.FieldStatus] {

  val emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

  override def channelRead0(ctx: ChannelHandlerContext, msg: FieldStatus){
    val player = NailedAPI.getPlayerRegistry.getPlayer(NailedNetworkHandler.getPlayer(ctx))
    var color = 'r'
    var info = ""
    var send = true
    msg.field match {
      case 0 => //Username
        if(msg.content.length() >= 2){
          IpcManager.instance().sendPacket(new PacketCheckAccount(player.getId, msg.content, 0))
          send = false
        }else{
          color = 'c'
          info = "Too short!"
        }
      case 1 => //Email
        if(emailPattern.matcher(msg.content).find()){
          IpcManager.instance().sendPacket(new PacketCheckAccount(player.getId, msg.content, 1))
          send = false
        }else{
          color = 'c'
          info = "Not a valid email"
        }
      case 2 => //Name
        if(msg.content.length() >= 2){
          color = 'a'
          info = "OK!"
        }else{
          color = 'c'
          info = "Too short!"
        }
      case 3 | 4 => //Password & PasswordConfirm
        if(msg.content.length() >= 6){
          color = 'a'
          info = "OK"
        }else{
          color = 'c'
          info = "Too short!"
        }
    }
    if(send) ctx.writeAndFlush(new NailedPacket.FieldStatus(msg.field, info, color))
  }
}

@Sharable
object FMLHandshakeHandler extends ChannelInboundHandlerAdapter {

  override def userEventTriggered(ctx: ChannelHandlerContext, evt: scala.Any){
    evt match {
      case e: NetworkHandshakeEstablished =>
        val channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER)
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER)
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(e.dispatcher)
        NailedAPI.getMapLoader.getMaps.filter(m => m.getID < 1 | m.getID > 1).foreach(m =>
          channel.writeOutbound(new ForgeMessage.DimensionRegisterMessage(m.getID, 0))
        )
      case _ =>
    }
    ctx.fireUserEventTriggered(evt)
  }
}
