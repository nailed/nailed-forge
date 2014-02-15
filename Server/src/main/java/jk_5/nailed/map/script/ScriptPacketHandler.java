package jk_5.nailed.map.script;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.network.NailedNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacketHandler<T> extends SimpleChannelInboundHandler<T> {

    public static class RequestUpdateHandler extends ScriptPacketHandler<ScriptPacket.RequestTerminalUpdate>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.RequestTerminalUpdate msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            ((TerminalSynchronizer) p.getCurrentMap().getMachineSynchronizer()).updateClient(player);
        }
    }

    public static class ClientKeyTypedHandler extends ScriptPacketHandler<ScriptPacket.ClientKeyTyped>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.ClientKeyTyped msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            ((MachineSynchronizer) p.getCurrentMap().getMachineSynchronizer()).pressKey(msg.ch, msg.key);
        }
    }

    public static class ClientMouseClickedHandler extends ScriptPacketHandler<ScriptPacket.ClientMouseClicked>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.ClientMouseClicked msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            ((MachineSynchronizer) p.getCurrentMap().getMachineSynchronizer()).clickMouse(msg.x, msg.y, msg.button);
        }
    }

    public static class ClientStringTypedHandler extends ScriptPacketHandler<ScriptPacket.ClientStringTyped>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.ClientStringTyped msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            ((MachineSynchronizer) p.getCurrentMap().getMachineSynchronizer()).typeString(msg.data);
        }
    }

    public static class ClientInterruptHandler extends ScriptPacketHandler<ScriptPacket.Interrupt>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.Interrupt msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            MachineSynchronizer synchronizer = (MachineSynchronizer) p.getCurrentMap().getMachineSynchronizer();
            if(msg.data == 0){
                synchronizer.terminate();
            }else if(msg.data == 1){
                synchronizer.reboot();
            }else if(msg.data == 2){
                synchronizer.shutdown();
            }
        }
    }

    public static class FireClientEventHandler extends ScriptPacketHandler<ScriptPacket.ClientEvent>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.ClientEvent msg) throws Exception{
            EntityPlayer player = NailedNetworkHandler.getPlayer(ctx);
            Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
            ((MachineSynchronizer) p.getCurrentMap().getMachineSynchronizer()).fireEvent(msg.event);
        }
    }
}
