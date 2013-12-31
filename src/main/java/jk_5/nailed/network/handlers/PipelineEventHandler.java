package jk_5.nailed.network.handlers;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkHandshakeEstablished;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.server.ProxyCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.network.ForgeMessage;

/**
 * No description given
 *
 * @author jk-5
 */
public class PipelineEventHandler extends ChannelInboundHandlerAdapter {

    private boolean connected = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        if(evt instanceof NetworkHandshakeEstablished){
            NetworkDispatcher dispatcher = ((NetworkHandshakeEstablished) evt).dispatcher;
            EntityPlayer player = ReflectionHelper.getPrivateValue(NetworkDispatcher.class, dispatcher, "player");
            for(Map map : MapLoader.instance().getMaps()){
                ForgeMessage.DimensionRegisterMessage packet = new ForgeMessage.DimensionRegisterMessage();
                ReflectionHelper.setPrivateValue(ForgeMessage.DimensionRegisterMessage.class, packet, ProxyCommon.providerID, "providerId");
                ReflectionHelper.setPrivateValue(ForgeMessage.DimensionRegisterMessage.class, packet, map.getID(), "dimensionId");
                EmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
                channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
                channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
                channel.writeOutbound(packet);
            }
            //if(event.player.worldObj.provider instanceof NailedWorldProvider){
            //    //TODO: send data?
            //}
        }else if(evt instanceof FMLNetworkEvent.ClientConnectedToServerEvent){
            this.connected = true;
        }else if(evt instanceof FMLNetworkEvent.ClientDisconnectionFromServerEvent){
            if(this.connected){
                NailedModContainer.getInstance().unregisterDimensions();
                this.connected = false;
            }
        }

        ctx.fireUserEventTriggered(evt);
    }
}
