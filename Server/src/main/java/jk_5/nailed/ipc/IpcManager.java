package jk_5.nailed.ipc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jk_5.nailed.NailedServer;
import jk_5.nailed.ipc.packet.IpcPacket;
import jk_5.nailed.util.config.ConfigTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.channels.UnresolvedAddressException;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcManager {

    public static final Logger logger = LogManager.getLogger("Nailed|IPC");

    private static final IpcManager instance = new IpcManager();
    private Channel channel;

    private final boolean enabled;
    private final String host;
    private final int port;

    public static void main(String[] args){
        IpcManager.instance().start();
    }

    public IpcManager() {
        if(NailedServer.getConfig() == null){
            this.enabled = true;
            this.host = "127.0.0.1";
            this.port = 9001;
        }else{
            ConfigTag config = NailedServer.getConfig().getTag("IPC").useBraces();
            this.enabled = config.getTag("enabled").getBooleanValue(false);
            this.host = config.getTag("host").getValue("127.0.0.1");
            this.port = config.getTag("port").getIntValue(9001);
        }
    }

    public static IpcManager instance(){
        return instance;
    }

    public void start(){
        if(!this.enabled) return;
        logger.info("Starting IPC client");
        final EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new Pipeline());
            this.channel = bootstrap.connect(this.host, this.port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception{
                    logger.info("Connected to the nailed-web server");
                }
            }).channel();
            this.channel.closeFuture().addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Connection closed");
                    group.shutdownGracefully();
                }
            });
        }catch(UnresolvedAddressException e){
            logger.error("Could not resolve address for IPC server");
        }
    }

    public ChannelFuture close(){
        if(this.channel != null && this.channel.isOpen()){
            return this.channel.close();
        }
        return null;
    }

    public ChannelFuture sendPacket(IpcPacket packet){
        if(this.enabled && this.channel.isOpen()){
            return channel.writeAndFlush(packet);
        }
        return null;
    }

    public boolean isConnected(){
        return this.enabled && this.channel.isOpen();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
