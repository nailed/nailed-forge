package jk_5.nailed.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.network.handlers.GuiReturnDataHandler;
import jk_5.nailed.network.handlers.MovementEventHandler;
import jk_5.nailed.network.handlers.PipelineEventHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler {

    private static FMLEmbeddedChannel channel;

    public static void registerChannel(){
        channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec()).get(Side.SERVER);

        ChannelPipeline pipeline = channel.pipeline();
        String targetName = channel.findChannelHandlerNameForType(NailedPacketCodec.class);

        pipeline.addAfter(targetName, "ServerToClientConnection", new PipelineEventHandler());
        pipeline.addAfter(targetName, "MovementEventHandler", new MovementEventHandler());
        pipeline.addAfter(targetName, "GuiReturnDataHandler", new GuiReturnDataHandler());
    }

    public static void sendPacketToAllPlayersInDimension(NailedPacket packet, int dimension){
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channel.writeOutbound(packet);
    }

    public static void sendPacketToPlayer(NailedPacket packet, EntityPlayer player){
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channel.writeOutbound(packet);
    }
}
