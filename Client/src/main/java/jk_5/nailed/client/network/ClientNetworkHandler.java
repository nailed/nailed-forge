package jk_5.nailed.client.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.client.network.handlers.*;
import jk_5.nailed.client.scripting.ScriptPacketHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.network.NailedPacketCodec;

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
        pipeline.addAfter(targetName, "TimeUpdateHandler", new TimeUpdateHandler());
        pipeline.addAfter(targetName, "SkinDataHandler", new SkinDataHandler());
        pipeline.addAfter(targetName, "StoreSkinHandler", new StoreSkinHandler());
        pipeline.addAfter(targetName, "MapDataHandler", new MapDataHandler());
        pipeline.addAfter(targetName, "ParticleHandler", new ParticleHandler());
        pipeline.addAfter(targetName, "TerminalGuiHandler", new TerminalGuiHandler());
        pipeline.addAfter(targetName, "MapEditHandler", new MapEditHandler());

        pipeline.addAfter(targetName, "Script-MachineUpdateHandler", new ScriptPacketHandler.MachineUpdateHandler());
    }

    public static void sendPacketToServer(NailedPacket packet){
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channel.writeOutbound(packet);
    }
}
