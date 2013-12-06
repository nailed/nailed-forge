package jk_5.nailed.ipc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class Pipeline extends ChannelInitializer<SocketChannel> {

    private final IpcHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipe = ch.pipeline();

        pipe.addLast("http-codec", new HttpClientCodec());
        pipe.addLast("http-aggregator", new HttpObjectAggregator(65536));
        pipe.addLast("websocket-handler", new WebsocketHandler());
        pipe.addLast("ipc-handler", handler);
    }
}
