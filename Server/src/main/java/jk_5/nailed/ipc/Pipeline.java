package jk_5.nailed.ipc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import jk_5.nailed.ipc.codec.PacketCodec;
import jk_5.nailed.ipc.codec.VarintFrameCodec;
import jk_5.nailed.ipc.handler.HandshakeHandler;
import jk_5.nailed.ipc.handler.PacketHandler;

/**
 * No description given
 *
 * @author jk-5
 */
public class Pipeline extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();
        pipe.addLast("framer", new VarintFrameCodec());
        pipe.addLast("packetCodec", new PacketCodec());
        pipe.addLast("handshakeHandler", new HandshakeHandler());
        pipe.addLast("packetHandler", new PacketHandler());
    }
}
