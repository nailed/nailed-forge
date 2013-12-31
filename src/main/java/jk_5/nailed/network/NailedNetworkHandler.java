package jk_5.nailed.network;

import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.network.handlers.MovementEventHandler;
import jk_5.nailed.network.handlers.NotificationHandler;
import jk_5.nailed.network.handlers.PipelineEventHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumMap;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler {

    private static EnumMap<Side, EmbeddedChannel> channelPair;

    public static EmbeddedChannel getChannelForSide(Side side){
        return channelPair.get(side);
    }

    public static void registerChannel(Side side){
        channelPair = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec());

        if (side == Side.CLIENT){
            addClientHandlers();
        }

        ChannelPipeline pipeline = channelPair.get(Side.SERVER).pipeline();

        pipeline.addAfter("jk_5.nailed.network.NailedPacketCodec#0", "ServerToClientConnection", new PipelineEventHandler());
        pipeline.addAfter("jk_5.nailed.network.NailedPacketCodec#0", "MovementEventHandler", new MovementEventHandler());
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers(){
        ChannelPipeline pipeline = channelPair.get(Side.CLIENT).pipeline();

        pipeline.addAfter("jk_5.nailed.network.NailedPacketCodec#0", "NotificationHandler", new NotificationHandler());
    }

    public static void sendPacketToServer(NailedPacket packet){
        EmbeddedChannel channel = getChannelForSide(Side.CLIENT);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channel.writeOutbound(packet);
    }

    public static void sendPacketToPlayer(NailedPacket packet, EntityPlayer player){
        EmbeddedChannel channel = getChannelForSide(Side.SERVER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channel.writeOutbound(packet);
    }
}
