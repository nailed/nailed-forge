package jk_5.nailed.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import jk_5.nailed.api.concurrent.Callback;
import org.apache.commons.lang3.Validate;

import java.net.URI;

/**
 * No description given
 *
 * @author jk-5
 */
public class HttpClient {

    public static int timeout = 5000;

    public static void get(String url, EventLoop eventLoop, final Callback<String> callback){
        Validate.notNull(url, "url");
        Validate.notNull(eventLoop, "eventLoop");
        Validate.notNull(callback, "callback");

        final URI uri = URI.create(url);

        Validate.notNull(uri.getScheme(), "scheme");
        Validate.notNull(uri.getHost(), "host");
        boolean ssl = uri.getScheme().equals("https");
        int port = uri.getPort();
        if(port == -1){
            if(ssl){
                port = 443;
            }else if(uri.getScheme().equals("http")){
                port = 80;
            }else throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
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
