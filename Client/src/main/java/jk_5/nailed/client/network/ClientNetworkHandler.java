package jk_5.nailed.client.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.client.network.handlers.NotificationHandler;
import jk_5.nailed.client.network.handlers.OpenGuiHandler;

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

        //pipeline.addAfter(targetName, "ServerToClientConnection", new PipelineEventHandler());
        //pipeline.addAfter(targetName, "MovementEventHandler", new MovementEventHandler());
        //pipeline.addAfter(targetName, "GuiReturnDataHandler", new GuiReturnDataHandler());

        pipeline.addAfter(targetName, "NotificationHandler", new NotificationHandler());
        pipeline.addAfter(targetName, "OpenGuiHandler", new OpenGuiHandler());

        //channel.generatePacketFrom(new NailedPacket.TileEntityData());
    }

    public static void sendPacketToServer(NailedPacket packet){
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channel.writeOutbound(packet);
    }
}
