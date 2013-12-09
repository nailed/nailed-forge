package jk_5.nailed.ipc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import jk_5.nailed.ipc.packet.PacketInitConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * No description given
 *
 * @author jk-5
 */
public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

    public static final Logger logger = Logger.getLogger("Nailed|IPC");

    private final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(IpcManager.instance().getUri(), WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
    private ChannelPromise handshakeFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!this.handshaker.isHandshakeComplete()){
            this.handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            logger.info("Connected to IPC server!");
            this.handshakeFuture.setSuccess();
            ctx.writeAndFlush(new PacketInitConnection());
            return;
        }
        if(msg instanceof CloseWebSocketFrame){
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warning("IPC connection closed!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(handshakeFuture.isDone()) handshakeFuture.setFailure(cause);
        ctx.close();
        logger.log(Level.SEVERE, "Error while handshaking with IPC server", cause);
    }
}
