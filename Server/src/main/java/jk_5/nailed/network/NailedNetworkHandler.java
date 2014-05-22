package jk_5.nailed.network;

import com.google.common.base.Joiner;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.player.PlayerClient;
import jk_5.nailed.map.script.ScriptPacketHandler;
import jk_5.nailed.network.handlers.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler {

    private static FMLEmbeddedChannel channel;
    private static Map<NetworkDispatcher, Map<String, String>> clientMods = new MapMaker().weakKeys().makeMap();

    private static final Class<?> connType;
    private static final Method acceptVanillaMethod;
    private static final Object vanillaConnType;

    static {
        try{
            connType = Class.forName("cpw.mods.fml.common.network.handshake.NetworkDispatcher$ConnectionType");
            acceptVanillaMethod = NetworkDispatcher.class.getDeclaredMethod("completeServerSideConnection", connType);
            acceptVanillaMethod.setAccessible(true);
            Field vanillaField = connType.getDeclaredField("VANILLA");
            vanillaField.setAccessible(true);
            vanillaConnType = vanillaField.get(null);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

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

    @SuppressWarnings("unused")
    //Called from NetworkDispatcher.completeServerSideConnection
    public static void onConnected(NetworkDispatcher dispatcher, String connectionType){
        EntityPlayerMP playerEnt = ((NetHandlerPlayServer) dispatcher.manager.getNetHandler()).playerEntity;
        Player player = NailedAPI.getPlayerRegistry().getOrCreatePlayer(playerEnt.getGameProfile());
        Map<String, String> mods = clientMods.get(dispatcher);
        try{
            if(connectionType.equals("VANILLA")){
                player.setClient(PlayerClient.VANILLA);
            }else{
                if(mods == null){
                    dispatcher.rejectHandshake("Not a vanilla client but no modlist was sent? Wtf are you?");
                    return;
                }
                if(mods.containsKey("Nailed")){
                    player.setClient(PlayerClient.NAILED);
                }else if(mods.containsKey("Forge")){
                    player.setClient(PlayerClient.FORGE);
                }else if(mods.containsKey("FML")){
                    player.setClient(PlayerClient.FML);
                }else{
                    dispatcher.rejectHandshake("Unsupported client. Use FML, Forge or the Nailed client");
                    return;
                }
            }
        }finally{
            clientMods.remove(dispatcher);
        }
        NailedLog.info("{} client connected", player.getClient().name());
        ChannelPipeline pipe = dispatcher.manager.channel().pipeline();
        pipe.addAfter("encoder", "NailedPacketAdapter", new MinecraftPacketAdapter(playerEnt));
    }

    @SuppressWarnings("unused")
    //Called from FMLHandshakeServerState.HELLO.accept Under FMLLog.info("Client attempting to join with %d mods : %s"
    public static void onClientModList(ChannelHandlerContext ctx, Map<String, String> mods){
        NailedLog.info("Received client modlist: {}", Joiner.on(',').withKeyValueSeparator(":").join(mods));
        clientMods.put(ctx.channel().attr(NetworkDispatcher.FML_DISPATCHER).get(), mods);
    }

    @SuppressWarnings("unused")
    //Called from NetworkDispatcher$VanillaTimeoutWaiter.handlerAdded
    public static void acceptVanilla(final NetworkDispatcher dispatcher){
        NailedAPI.getScheduler().runTask(new NailedRunnable() {
            @Override
            public void run() {
                try {
                    acceptVanillaMethod.invoke(dispatcher, vanillaConnType);
                } catch (Exception e) {
                    NailedLog.error("Error while accepting vanilla connection", e);
                }
            }
        });
    }
}
