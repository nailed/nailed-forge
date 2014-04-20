package jk_5.nailed.client.network

import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry, FMLEmbeddedChannel}
import jk_5.nailed.network.{NailedPacket, NailedPacketCodec}
import cpw.mods.fml.relauncher.Side
import jk_5.nailed.client.scripting.ScriptPacketHandler

/**
 * No description given
 *
 * @author jk-5
 */
object ClientNetworkHandler {
  private var channel: FMLEmbeddedChannel = null

  def registerChannel(){
    this.channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec).get(Side.CLIENT)

    val pipeline = channel.pipeline
    val targetName = channel.findChannelHandlerNameForType(classOf[NailedPacketCodec])
    pipeline.addAfter(targetName, "NotificationHandler", NotificationHandler)
    pipeline.addAfter(targetName, "OpenGuiHandler", OpenGuiHandler)
    pipeline.addAfter(targetName, "TileEntityDataHandler", TileEntityDataHandler)
    pipeline.addAfter(targetName, "TimeUpdateHandler", TimeUpdateHandler)
    pipeline.addAfter(targetName, "SkinDataHandler", SkinDataHandler)
    pipeline.addAfter(targetName, "StoreSkinHandler", StoreSkinHandler)
    pipeline.addAfter(targetName, "MapDataHandler", MapDataHandler)
    pipeline.addAfter(targetName, "ParticleHandler", ParticleHandler)
    pipeline.addAfter(targetName, "TerminalGuiHandler", TerminalGuiHandler)
    pipeline.addAfter(targetName, "MapEditHandler", MapEditHandler)
    pipeline.addAfter(targetName, "RegisterAchievementHandler", RegisterAchievementHandler)
    pipeline.addAfter(targetName, "ClientUpdateHandler", ClientUpdateHandler)
    pipeline.addAfter(targetName, "DisplayLoginHandler", DisplayLoginHandler)
    pipeline.addAfter(targetName, "LoginResponseHandler", LoginResponseHandler)
    pipeline.addAfter(targetName, "FieldStatusHandler", FieldStatusHandler)

    pipeline.addAfter(targetName, "Script-MachineUpdateHandler", new ScriptPacketHandler.MachineUpdateHandler)
  }

  def sendPacketToServer(packet: NailedPacket){
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
    channel.writeOutbound(packet)
  }
}
