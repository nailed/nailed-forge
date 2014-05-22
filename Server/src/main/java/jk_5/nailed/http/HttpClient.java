package jk_5.nailed.http;

import java.net.*;

import org.apache.commons.lang3.*;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.http.*;

import jk_5.nailed.api.concurrent.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class HttpClient {

    public static int timeout = 5000;

    private HttpClient(){

    }

    public static void get(String url, EventLoop eventLoop, final Callback<String> callback) {
        Validate.notNull(url, "url");
        Validate.notNull(eventLoop, "eventLoop");
        Validate.notNull(callback, "callback");

        final URI uri = URI.create(url);

        Validate.notNull(uri.getScheme(), "scheme");
        Validate.notNull(uri.getHost(), "host");
        boolean ssl = "https".equals(uri.getScheme());
        int port = uri.getPort();
        if(port == -1){
            if(ssl){
                port = 443;
            }else if("http".equals(uri.getScheme())){
                port = 80;
            }else{
                throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
            }
        }

        ChannelFutureListener future = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    String path = uri.getRawPath() + ((uri.getRawQuery() == null) ? "" : "?" + uri.getRawQuery());
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                    request.headers().set(HttpHeaders.Names.HOST, uri.getHost());
                    future.channel().writeAndFlush(request);
                }else{
                    callback.callback(null);
                }
            }
        };

        new Bootstrap().channel(NioSocketChannel.class).group(eventLoop).handler(new HttpInitializer(callback, ssl)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout).remoteAddress(uri.getHost(), port).connect().addListener(future);
    }
}
