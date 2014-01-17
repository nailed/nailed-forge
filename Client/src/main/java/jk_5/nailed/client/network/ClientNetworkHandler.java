package jk_5.nailed.client.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.client.network.handlers.NotificationHandler;
import jk_5.nailed.client.network.handlers.OpenGuiHandler;
import jk_5.nailed.client.network.handlers.TileEntityDataHandler;

/**
 * No description given
 *
 * @author jk-5
 */
public class ClientNetworkHandler {

    private static FMLEmbeddedChannel channel;

    public static void registerChannel(){
        channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec()).get(Side.CLIENT);

        ChannelPipeline pipeline = channel.pipeline();
        String targetName = channel.findChannelHandlerNameForType(NailedPacketCodec.class);

        pipeline.addAfter(targetName, "NotificationHandler", new NotificationHandler());
        pipeline.addAfter(targetName, "OpenGuiHandler", new OpenGuiHandler());
        pipeline.addAfter(targetName, "TileEntityDataHandler", new TileEntityDataHandler());
    }

    public static void sendPacketToServer(NailedPacket packet){
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channel.writeOutbound(packet);
    }
}
