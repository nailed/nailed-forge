package jk_5.nailed.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.map.script.ScriptPacketHandler;
import jk_5.nailed.network.handlers.FMLHandshakeHandler;
import jk_5.nailed.network.handlers.FPSSummaryHandler;
import jk_5.nailed.network.handlers.GuiReturnDataHandler;
import jk_5.nailed.network.handlers.MovementEventHandler;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler {

    @Getter
    private static FMLEmbeddedChannel channel;

    public static void registerChannel(){
        channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec()).get(Side.SERVER);

        ChannelPipeline pipeline = channel.pipeline();
        String targetName = channel.findChannelHandlerNameForType(NailedPacketCodec.class);

        pipeline.addAfter(targetName, "FMLHandshakeHandler", new FMLHandshakeHandler());
        pipeline.addAfter(targetName, "MovementEventHandler", new MovementEventHandler());
        pipeline.addAfter(targetName, "GuiReturnDataHandler", new GuiReturnDataHandler());
        pipeline.addAfter(targetName, "FPSSummaryHandler", new FPSSummaryHandler());

        pipeline.addAfter(targetName, "Script-QueueEventHandler", new ScriptPacketHandler.QueueEventHandler());
        pipeline.addAfter(targetName, "Script-StateEventHandler", new ScriptPacketHandler.StateEventHandler());
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

    public static EntityPlayerMP getPlayer(ChannelHandlerContext ctx){
        return ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
    }

    public static Packet getProxyPacket(NailedPacket packet){
        return channel.generatePacketFrom(packet);
    }
}
