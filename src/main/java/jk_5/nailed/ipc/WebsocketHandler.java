package jk_5.nailed.ipc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * No description given
 *
 * @author jk-5
 */
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof CloseWebSocketFrame){
            ctx.close();
        }
    }
}
