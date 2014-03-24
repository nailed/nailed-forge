package jk_5.nailed.client.scripting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.NailedClient;
import jk_5.nailed.map.script.ScriptPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacketHandler<T> extends SimpleChannelInboundHandler<T> {

    public static class MachineUpdateHandler extends ScriptPacketHandler<ScriptPacket.UpdateMachine> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.UpdateMachine msg) throws Exception{
            ClientMachine machine = NailedClient.machines().get(msg.instanceId);
            if(machine != null){
                machine.readData(msg.data);
            }
        }
    }
}
