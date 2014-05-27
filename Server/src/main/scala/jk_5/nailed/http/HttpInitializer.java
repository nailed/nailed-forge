package jk_5.nailed.http;

import java.util.concurrent.*;
import javax.net.ssl.*;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.*;
import io.netty.handler.timeout.*;

import jk_5.nailed.api.concurrent.*;

/**
 * No description given
 *
 * @author jk-5
 */
class HttpInitializer extends ChannelInitializer<Channel> {

    private final Callback<String> callback;
    private final boolean ssl;

    HttpInitializer(Callback<String> callback, boolean ssl) {
        this.callback = callback;
        this.ssl = ssl;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("timeout", new ReadTimeoutHandler(HttpClient.timeout, TimeUnit.MILLISECONDS));
        if(ssl){
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{
                    TrustingX509Manager.getInstance()
            }, null);

            SSLEngine engine = context.createSSLEngine();
            engine.setUseClientMode(true);

            ch.pipeline().addLast("ssl", new SslHandler(engine));
        }
        ch.pipeline().addLast("http", new HttpClientCodec());
        ch.pipeline().addLast("handler", new HttpHandler(callback));
    }
}
