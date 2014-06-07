package jk_5.nailed.network.minecraft

import io.netty.channel.{ChannelOption, ChannelInitializer, Channel}
import net.minecraft.network.{NetworkManager, PingResponseHandler, NetworkSystem}
import io.netty.handler.timeout.ReadTimeoutHandler
import cpw.mods.fml.common.network.internal.FMLNetworkHandler
import net.minecraft.util.{MessageSerializer, MessageSerializer2, MessageDeserializer, MessageDeserializer2}
import net.minecraft.server.network.NetHandlerHandshakeTCP
import net.minecraft.server.MinecraftServer
import io.netty.buffer.PooledByteBufAllocator

/**
 * No description given
 *
 * @author jk-5
 */
class MinecraftChannelInitializer(val system: NetworkSystem) extends ChannelInitializer[Channel] {

  lazy val managers = {
    val f = classOf[NetworkSystem].getDeclaredField("networkManagers")
    f.setAccessible(true)
    f.get(this.system).asInstanceOf[java.util.List[NetworkManager]]
  }

  override def initChannel(ch: Channel){
    try{
      ch.config().setOption(ChannelOption.IP_TOS, 24: Integer)
    }catch{case e: Exception =>}
    try{
      ch.config().setOption(ChannelOption.TCP_NODELAY, false: java.lang.Boolean)
    }catch{case e: Exception =>}
    try{
      ch.config().setAllocator(PooledByteBufAllocator.DEFAULT)
    }catch{case e: Exception =>}

    val manager = new NetworkManager(false)
    this.managers.add(manager)

    val pipe = ch.pipeline()
    pipe.addLast("timeout", new ReadTimeoutHandler(FMLNetworkHandler.READ_TIMEOUT))
    pipe.addLast("legacy_query", new PingResponseHandler(this.system))
    pipe.addLast("splitter", new MessageDeserializer2)
    pipe.addLast("decoder", new MessageDeserializer)
    pipe.addLast("prepender", new MessageSerializer2)
    pipe.addLast("encoder", new MessageSerializer)
    pipe.addLast("NailedPacketAdapter", new MinecraftPacketAdapter)
    pipe.addLast("packet_handler", manager)

    manager.setNetHandler(new NetHandlerHandshakeTCP(MinecraftServer.getServer, manager))
  }
}
