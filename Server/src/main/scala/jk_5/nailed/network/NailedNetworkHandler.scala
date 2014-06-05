package jk_5.nailed.network

import cpw.mods.fml.common.network.{FMLOutboundHandler, NetworkRegistry, FMLEmbeddedChannel}
import cpw.mods.fml.relauncher.Side
import jk_5.nailed.map.script.ScriptPacketHandler
import com.google.common.collect.MapMaker
import cpw.mods.fml.common.network.handshake.NetworkDispatcher
import net.minecraft.entity.player.EntityPlayer
import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.NetHandlerPlayServer
import jk_5.nailed.api.NailedAPI
import jk_5.nailed.api.player.PlayerClient
import jk_5.nailed.NailedLog
import jk_5.nailed.network.minecraft.MinecraftPacketAdapter
import com.google.common.base.Joiner
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable

/**
 * No description given
 *
 * @author jk-5
 */
object NailedNetworkHandler {
  var channel: FMLEmbeddedChannel = _

  def registerChannel(){
    this.channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec).get(Side.SERVER)

    val pipeline = channel.pipeline
    val targetName = channel.findChannelHandlerNameForType(classOf[NailedPacketCodec])
    pipeline.addAfter(targetName, "FMLHandshakeHandler", FMLHandshakeHandler)
    pipeline.addAfter(targetName, "GuiReturnDataHandler", GuiReturnDataHandler)
    pipeline.addAfter(targetName, "FPSSummaryHandler", FPSHandler)
    pipeline.addAfter(targetName, "LoginHandler", LoginHandler)
    pipeline.addAfter(targetName, "FieldStatusHandler", FieldStatusHandler)
    pipeline.addAfter(targetName, "RegisterHandler", RegisterHandler)

    pipeline.addAfter(targetName, "Script-QueueEventHandler", new ScriptPacketHandler.QueueEventHandler)
    pipeline.addAfter(targetName, "Script-StateEventHandler", new ScriptPacketHandler.StateEventHandler)
  }

  val clientMods = new MapMaker().weakKeys().makeMap[NetworkDispatcher, java.util.Map[String, String]]()

  val connType = Class.forName("cpw.mods.fml.common.network.handshake.NetworkDispatcher$ConnectionType")
  val acceptVanillaMethod = {
    val m = classOf[NetworkDispatcher].getDeclaredMethod("completeServerSideConnection", connType)
    m.setAccessible(true)
    m
  }
  val vanillaConnType = {
    val f = connType.getDeclaredField("VANILLA")
    f.setAccessible(true)
    f.get(null)
  }

  def sendPacketToAllPlayersInDimension(packet: NailedPacket, dimension: Int){
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension: java.lang.Integer)
    channel.writeOutbound(packet)
  }

  def sendPacketToPlayer(packet: NailedPacket, player: EntityPlayer){
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER)
    channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
    channel.writeOutbound(packet)
  }

  @inline def getPlayer(ctx: ChannelHandlerContext) =
    ctx.channel().attr(NetworkRegistry.NET_HANDLER).get().asInstanceOf[NetHandlerPlayServer].playerEntity

  @inline def getProxyPacket(packet: NailedPacket) = channel.generatePacketFrom(packet)

  //Called from NetworkDispatcher.completeServerSideConnection
  def onConnected(dispatcher: NetworkDispatcher, connectionType: String){
    val playerEnt = dispatcher.manager.getNetHandler.asInstanceOf[NetHandlerPlayServer].playerEntity
    val player = NailedAPI.getPlayerRegistry.getOrCreatePlayer(playerEnt.getGameProfile)
    val mods = clientMods.get(dispatcher)
    try{
      if("VANILLA".equals(connectionType)){
        player.setClient(PlayerClient.VANILLA)
      }else{
        if(mods == null){
          dispatcher.rejectHandshake("Not a vanilla client but no modlist was sent? Wtf are you?")
          return
        }
        if(mods.containsKey("Nailed")){
          player.setClient(PlayerClient.NAILED)
        }else if(mods.containsKey("Forge")){
          player.setClient(PlayerClient.FORGE)
        }else if(mods.containsKey("FML")){
          player.setClient(PlayerClient.FML)
        }else{
          dispatcher.rejectHandshake("Unsupported client. Use FML, Forge or the Nailed client")
          return
        }
      }
    }finally{
      clientMods.remove(dispatcher)
    }
    NailedLog.info("{} client connected", player.getClient.name)

    val pipeline = dispatcher.manager.channel.pipeline
    pipeline.get("NailedPacketAdapter").asInstanceOf[MinecraftPacketAdapter].player = playerEnt
  }

  //Called from FMLHandshakeServerState.HELLO.accept Under FMLLog.info("Client attempting to join with %d mods : %s"
  def onClientModList(ctx: ChannelHandlerContext, mods: java.util.Map[String, String]){
    NailedLog.info("Received client modlist: {}", Joiner.on(',').withKeyValueSeparator(":").join(mods))
    clientMods.put(ctx.channel.attr(NetworkDispatcher.FML_DISPATCHER).get, mods)
  }

  //Called from NetworkDispatcher$VanillaTimeoutWaiter.handlerAdded
  def acceptVanilla(dispatcher: NetworkDispatcher){
    NailedAPI.getScheduler.runTask(new NailedRunnable {
      override def run() = try{
        acceptVanillaMethod.invoke(dispatcher, vanillaConnType)
      }catch{
        case e: Exception => NailedLog.error("Error while accepting vanilla connection", e)
      }
    })
  }
}
