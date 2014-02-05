package jk_5.nailed.network;

import cpw.mods.fml.relauncher.ReflectionHelper;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import jk_5.nailed.NailedLog;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.sign.Sign;
import jk_5.nailed.map.sign.SignCommandHandler;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S33PacketUpdateSign;
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
            NetworkManager manager = ctx.pipeline().get(NetworkManager.class);
            EntityPlayerMP player = ((NetHandlerPlayServer) manager.func_150729_e()).field_147369_b;
            SignCommandHandler handler = MapLoader.instance().getMap(player.worldObj).getSignCommandHandler();
            handler.onSignAdded(packet.field_149590_d, packet.field_149593_a, packet.field_149591_b, packet.field_149592_c);
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        NetworkManager manager = (NetworkManager) ctx.pipeline().get("packet_handler");
        NetHandlerPlayServer handler = (NetHandlerPlayServer) manager.func_150729_e();
        EntityPlayerMP player = handler.field_147369_b;
        if(msg instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) msg;
            IChatComponent component = packet.field_148919_a;
            if(component instanceof ChatComponentTranslation){
                ChatComponentTranslation translation = (ChatComponentTranslation) component;
                String key = translation.func_150268_i();
                if(key.startsWith("death.")){
                    String died = ChatColor.stripColor(((ChatComponentText) translation.func_150271_j()[0]).func_150261_e());
                    if(died.startsWith("@")) died = died.substring(1);
                    EntityPlayerMP diedPlayer = PlayerRegistry.instance().getPlayerByUsername(died).getEntity();
                    if(player.dimension == diedPlayer.dimension){
                        NailedLog.info("Send death message for " + diedPlayer.getDisplayName() + " to " + player.getDisplayName());
                    }else return;
                }
            }
        }else if(msg instanceof S33PacketUpdateSign){
            S33PacketUpdateSign signPacket = (S33PacketUpdateSign) msg;
            String[] lines = ReflectionHelper.getPrivateValue(S33PacketUpdateSign.class, signPacket, "field_149349_d");
            if(lines[0].equalsIgnoreCase("$mappack")){
                Map map = MapLoader.instance().getMap(player.worldObj);
                Sign sign = map.getSignCommandHandler().getSign(signPacket.field_149352_a, signPacket.field_149350_b, signPacket.field_149351_c);
                if(sign == null){
                    ctx.write(msg, promise);
                    return;
                }
                ctx.write(sign.getUpdatePacket());
                return;
            }
        }
        ctx.write(msg, promise);
    }
}