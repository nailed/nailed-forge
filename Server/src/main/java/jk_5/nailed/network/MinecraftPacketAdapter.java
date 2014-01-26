package jk_5.nailed.network;

import cpw.mods.fml.relauncher.ReflectionHelper;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import jk_5.nailed.NailedLog;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
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
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        if(msg instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) msg;
            IChatComponent component = ReflectionHelper.getPrivateValue(S02PacketChat.class, packet, "field_148919_a");
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
