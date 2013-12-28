package jk_5.nailed.ipc;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.ipc.packet.IpcPacket;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.UnresolvedAddressException;
import java.util.logging.Logger;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcManager {

    public static final Logger logger = Logger.getLogger("Nailed|IPC");

    private static final IpcManager instance = new IpcManager();
    private Channel channel;

    @Getter private final boolean enabled = NailedModContainer.getConfig().getTag("IPC").useBraces().getTag("enabled").getBooleanValue(false);
    @Getter private final URI uri;

    public static void main(String[] args){
        IpcManager.instance().start();
    }

    static {
        FMLLog.makeLog("Nailed|IPC");
        MinecraftForge.EVENT_BUS.register(new IpcEventListener());
    }

    public IpcManager() {
        try{
            this.uri = new URI(NailedModContainer.getConfig().getTag("IPC").useBraces().getTag("url").getValue("ws://localhost/"));
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public static IpcManager instance(){
        return instance;
    }

    public void start(){
        logger.info("Starting IPC client");
        TickRegistry.registerScheduledTickHandler(new PacketProcessor(), Side.SERVER);
        final EventLoopGroup group = new NioEventLoopGroup();
        try{
            IpcHandler handler = new IpcHandler();
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new Pipeline(handler));
            this.channel = bootstrap.connect(this.uri.getHost(), this.uri.getPort()).channel();
            this.channel.closeFuture().addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    group.shutdownGracefully();
                }
            });
        //}catch(ConnectException e){
        //    NailedLog.severe("Was not able to connect to IPC server");
        }catch(UnresolvedAddressException e){
            logger.severe("Could not resolve address for IPC server");
        }
    }

    public void sendPacket(IpcPacket packet){
        if(this.enabled && this.channel.isOpen()){
            channel.writeAndFlush(packet);
        }
    }
}
