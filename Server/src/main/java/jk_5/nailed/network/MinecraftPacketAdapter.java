package jk_5.nailed.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.sign.Sign;
import jk_5.nailed.api.map.sign.SignCommandHandler;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.player.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftPacketAdapter extends ChannelDuplexHandler {

    /**
     * Adapt inbound packets
     *
     * @param ctx ChannelHandlerContext
     * @param msg The inbound packet
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(msg instanceof C12PacketUpdateSign){
            C12PacketUpdateSign packet = (C12PacketUpdateSign) msg;
            NetworkManager manager = ctx.pipeline().get(NetworkManager.class);
            EntityPlayerMP player = ((NetHandlerPlayServer) manager.getNetHandler()).playerEntity;
            SignCommandHandler handler = NailedAPI.getMapLoader().getMap(player.worldObj).getSignCommandHandler();
            handler.onSignAdded(packet.field_149590_d, packet.field_149593_a, packet.field_149591_b, packet.field_149592_c);
        }
        ctx.fireChannelRead(msg);
    }

    /**
     * Adapt outbound packets
     *
     * @param ctx ChannelHandlerContext
     * @param msg The outbound packet
     * @param promise The promise of the packet
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        NetworkManager manager = (NetworkManager) ctx.pipeline().get("packet_handler");
        NetHandlerPlayServer handler = (NetHandlerPlayServer) manager.getNetHandler();
        EntityPlayerMP player = handler.playerEntity;
        if(msg instanceof S02PacketChat){
            S02PacketChat packet = (S02PacketChat) msg;
            IChatComponent component = packet.field_148919_a;
            if(component instanceof ChatComponentTranslation){
                ChatComponentTranslation translation = (ChatComponentTranslation) component;
                String key = translation.getKey();
                if(key.startsWith("death.")){
                    String died = ChatColor.stripColor(((ChatComponentText) translation.getFormatArgs()[0]).getUnformattedTextForChat());
                    Player ply = NailedAPI.getPlayerRegistry().getPlayerByUsername(died);
                    if(ply == null){
                        ply = NailedAPI.getPlayerRegistry().getPlayerByUsername(died.substring(1));
                    }
                    EntityPlayerMP diedPlayer = ply.getEntity();
                    if(player.dimension == diedPlayer.dimension){
                        NailedLog.info("Sent death message for {} to {}", diedPlayer.getDisplayName().replace("%", "%%"), player.getDisplayName().replace("%", "%%"));
                    }else return;
                }
            }
        }else if(msg instanceof S33PacketUpdateSign){
            S33PacketUpdateSign signPacket = (S33PacketUpdateSign) msg;
            String[] lines = signPacket.field_149349_d;
            if(lines[0].equalsIgnoreCase("$mappack")){
                Map map = NailedAPI.getMapLoader().getMap(player.worldObj);
                Sign sign = map.getSignCommandHandler().getSign(signPacket.field_149352_a, signPacket.field_149350_b, signPacket.field_149351_c);
                if(sign == null){
                    ctx.write(msg, promise);
                    return;
                }
                ctx.write(sign.getUpdatePacket(), promise);
                return;
            }
        } else if(msg instanceof S38PacketPlayerListItem){
            S38PacketPlayerListItem playerList = (S38PacketPlayerListItem) msg;
            Player pPlayer = NailedAPI.getPlayerRegistry().getPlayerByUsername(playerList.func_149122_c());
            if (playerList.func_149121_d()){
                Player nPlayer = NailedAPI.getPlayerRegistry().getPlayer(player);
                if(!nPlayer.getPlayersVisible().contains(pPlayer)){
                    return;
                } else {
                    msg = new S38PacketPlayerListItem(pPlayer.getChatPrefix(), playerList.func_149121_d(), playerList.func_149120_e());
                    ctx.write(msg, promise);
                    return;
                }
            }
        }
        ctx.write(msg, promise);
    }
}
