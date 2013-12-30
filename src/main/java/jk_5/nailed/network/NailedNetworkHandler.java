package jk_5.nailed.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.network.handlers.NotificationHandler;
import jk_5.nailed.network.handlers.PipelineEventHandler;

import java.util.EnumMap;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler {

    private static EnumMap<Side, EmbeddedChannel> channelPair;

    public static void registerChannel(Side side){
        channelPair = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec());

        if (side == Side.CLIENT){
            addClientHandlers();
        }

        channelPair.get(Side.SERVER).pipeline().addAfter("jk_5.nailed.network.NailedPacketCodec#0", "ServerToClientConnection", new PipelineEventHandler());
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers(){
        channelPair.get(Side.CLIENT).pipeline().addAfter("jk_5.nailed.network.NailedPacketCodec#0", "NotificationHandler", new NotificationHandler());
    }
}
