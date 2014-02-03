package jk_5.nailed.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import jk_5.nailed.NailedLog;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftPacketAdapter extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(msg instanceof C12PacketUpdateSign){
            C12PacketUpdateSign packet = (C12PacketUpdateSign) msg;
            NailedLog.info("%s, %s, %s, %s  x=%s, y=%s, z=%s", packet.field_149590_d[0], packet.field_149590_d[1], packet.field_149590_d[2], packet.field_149590_d[3], Integer.toString(packet.field_149593_a), Integer.toString(packet.field_149591_b), Integer.toString(packet.field_149592_c));
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        if(msg instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) msg;
            IChatComponent component = packet.field_148919_a;
            if(component instanceof ChatComponentTranslation){
                ChatComponentTranslation translation = (ChatComponentTranslation) component;
                String key = translation.func_150268_i();
                if(key.startsWith("death.")){
                    String died = ChatColor.stripColor(((ChatComponentText) translation.func_150271_j()[0]).func_150261_e());
                    if(died.startsWith("@")) died = died.substring(1);
                    NetworkManager manager = (NetworkManager) ctx.pipeline().get("packet_handler");
                    NetHandlerPlayServer handler = (NetHandlerPlayServer) manager.func_150729_e();
                    EntityPlayerMP destPlayer = handler.field_147369_b;
                    EntityPlayerMP diedPlayer = PlayerRegistry.instance().getPlayerByUsername(died).getEntity();
                    if(destPlayer.dimension == diedPlayer.dimension){
                        NailedLog.info("Send death message for " + diedPlayer.getDisplayName() + " to " + destPlayer.getDisplayName());
                    }else return;
                }
            }
        }
        ctx.write(msg, promise);
    }
}
