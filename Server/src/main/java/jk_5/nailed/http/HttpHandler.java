package jk_5.nailed.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.*;

import jk_5.nailed.api.concurrent.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Callback<String> callback;
    private final StringBuilder buffer = new StringBuilder();

    public HttpHandler(Callback<String> callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpResponse){
            HttpResponse response = (HttpResponse) msg;
            int responseCode = response.getStatus().code();

            if(responseCode == HttpResponseStatus.NO_CONTENT.code()){
                done(ctx);
                return;
            }
        }
        if(msg instanceof HttpContent){
            HttpContent content = (HttpContent) msg;
            buffer.append(content.content().toString(CharsetUtil.UTF_8));
            if(msg instanceof LastHttpContent){
                done(ctx);
            }
        }
    }

    private void done(ChannelHandlerContext ctx) {
        try{
            callback.callback(buffer.toString());
        }finally{
            ctx.channel().close();
        }
    }
}
