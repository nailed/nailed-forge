package jk_5.nailed.network.handlers;

import java.util.regex.*;

import io.netty.channel.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.ipc.packet.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class FieldStatusHandler extends SimpleChannelInboundHandler<NailedPacket.FieldStatus> {

    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.FieldStatus msg) throws Exception {
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
