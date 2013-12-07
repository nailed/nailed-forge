package jk_5.nailed.ipc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jk_5.nailed.NailedLog;
import jk_5.nailed.NailedModContainer;

import java.nio.channels.UnresolvedAddressException;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcManager {

    private static final IpcManager instance = new IpcManager();
    private Channel channel;

    private final String host = NailedModContainer.getConfig().getTag("IPC").useBraces().getTag("host").getValue("localhost");
    private final int port = NailedModContainer.getConfig().getTag("IPC").useBraces().getTag("port").getIntValue(5000);

    public static IpcManager instance(){
        return instance;
    }

    public void start(){
        final EventLoopGroup group = new NioEventLoopGroup();
        try{
            IpcHandler handler = new IpcHandler();
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioServerSocketChannel.class);
            bootstrap.handler(new Pipeline(handler));
            this.channel = bootstrap.connect(this.host, this.port).channel();
            this.channel.closeFuture().addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    group.shutdownGracefully();
                }
            });
        //}catch(ConnectException e){
        //    NailedLog.severe("Was not able to connect to IPC server");
        }catch(UnresolvedAddressException e){
            NailedLog.severe("Could not resolve address for IPC server");
        }
    }
}
