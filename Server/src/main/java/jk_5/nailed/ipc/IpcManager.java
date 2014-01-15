package jk_5.nailed.ipc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jk_5.nailed.NailedServer;
import jk_5.nailed.ipc.packet.IpcPacket;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
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

    @Getter private final boolean enabled = NailedServer.getConfig().getTag("IPC").useBraces().getTag("enabled").getBooleanValue(false);
    @Getter private final URI uri;

    public static void main(String[] args){
        IpcManager.instance().start();
    }

    static {
        MinecraftForge.EVENT_BUS.register(new IpcEventListener());
        FMLCommonHandler.instance().bus().register(instance);
    }

    public IpcManager() {
        try{
            this.uri = new URI(NailedServer.getConfig().getTag("IPC").useBraces().getTag("url").getValue("ws://localhost/"));
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public static IpcManager instance(){
        return instance;
    }

    public void start(){
        logger.info("Starting IPC client");
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
            logger.error("Could not resolve address for IPC server");
        }
    }

    @SubscribeEvent
    public void processPackets(TickEvent.ServerTickEvent event){
        while(!PacketManager.getProcessQueue().isEmpty()){
            IpcPacket packet = PacketManager.getProcessQueue().poll();
            packet.processPacket();
        }
    }

    public void sendPacket(IpcPacket packet){
        if(this.enabled && this.channel.isOpen()){
            channel.writeAndFlush(packet);
        }
    }
}
