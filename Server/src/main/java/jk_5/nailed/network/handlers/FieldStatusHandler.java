package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.ipc.packet.PacketCheckAccount;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;

import java.util.regex.Pattern;

/**
 * No description given
 *
 * @author jk-5
 */
public class FieldStatusHandler extends SimpleChannelInboundHandler<NailedPacket.FieldStatus> {

    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.FieldStatus msg) throws Exception{
        Player player = NailedAPI.getPlayerRegistry().getPlayer(NailedNetworkHandler.getPlayer(ctx));
        char color = 'r';
        String info = "";
        switch(msg.field){
            case 0: //Username
                if(msg.content.length() >= 2){
                    IpcManager.instance().sendPacket(new PacketCheckAccount(player.getId(), msg.content, 0));
                    return;
                }else{
                    color = 'c';
                    info = "Too short!";
                }
                break;
            case 1: //Email
                if(emailPattern.matcher(msg.content).find()){
                    IpcManager.instance().sendPacket(new PacketCheckAccount(player.getId(), msg.content, 1));
                    return;
                }else{
                    color = 'c';
                    info = "Not a valid email";
                }
                break;
            case 2: //Name
                if(msg.content.length() >= 2){
                    color = 'a';
                    info = "OK!";
                }else{
                    color = 'c';
                    info = "Too short!";
                }
                break;
            case 3: //Password
            case 4: //PasswordConfirm
                if(msg.content.length() >= 6){
                    color = 'a';
                    info = "OK";
                }else{
                    color = 'c';
                    info = "Too short!";
                }
                break;
        }
        ctx.writeAndFlush(new NailedPacket.FieldStatus(msg.field, info, color));
    }
}
