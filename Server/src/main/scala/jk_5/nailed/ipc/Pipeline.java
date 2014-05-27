package jk_5.nailed.ipc;

import io.netty.channel.*;
import io.netty.channel.socket.*;

import jk_5.nailed.ipc.codec.*;
import jk_5.nailed.ipc.handler.*;

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
