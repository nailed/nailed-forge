package jk_5.nailed.network;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.map.script.ScriptPacketHandler;
import jk_5.nailed.network.handlers.*;
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

    private static FMLEmbeddedChannel channel;

    public static void registerChannel(){
        channel = NetworkRegistry.INSTANCE.newChannel("nailed", new NailedPacketCodec()).get(Side.SERVER);

        ChannelPipeline pipeline = channel.pipeline();
        String targetName = channel.findChannelHandlerNameForType(NailedPacketCodec.class);

        pipeline.addAfter(targetName, "FMLHandshakeHandler", new FMLHandshakeHandler());
        pipeline.addAfter(targetName, "MovementEventHandler", new MovementEventHandler());
        pipeline.addAfter(targetName, "GuiReturnDataHandler", new GuiReturnDataHandler());
        pipeline.addAfter(targetName, "FPSSummaryHandler", new FPSSummaryHandler());
        pipeline.addAfter(targetName, "LoginHandler", new LoginHandler());
        pipeline.addAfter(targetName, "FieldStatusHandler", new FieldStatusHandler());
        pipeline.addAfter(targetName, "RegisterHandler", new RegisterHandler());

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

    public static FMLEmbeddedChannel getChannel() {
        return channel;
    }

    public static void vanillaHandshake(NetworkDispatcher dispatcher, EntityPlayerMP player){
        NailedAPI.getPlayerRegistry().getOrCreatePlayer(player.getGameProfile()).setNailed(false);
        ChannelPipeline pipe = dispatcher.manager.channel().pipeline();
        pipe.addAfter("encoder", "NailedPacketAdapter", new MinecraftPacketAdapter(player));
    }
}
