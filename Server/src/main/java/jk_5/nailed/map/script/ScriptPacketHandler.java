package jk_5.nailed.map.script;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacketHandler<T> extends SimpleChannelInboundHandler<T> {

    public static class QueueEventHandler extends ScriptPacketHandler<ScriptPacket.QueueEvent>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.QueueEvent msg) throws Exception{
            ServerMachine machine = ServerMachine.REGISTRY.get(msg.instanceId);
            if(machine != null){
                machine.queueEvent(msg.eventName, msg.data);
            }
        }
    }

    public static class StateEventHandler extends ScriptPacketHandler<ScriptPacket.StateEvent>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.StateEvent msg) throws Exception{
            ServerMachine machine = ServerMachine.REGISTRY.get(msg.instanceId);
            if(machine != null){
                if(msg.operation == 0){
                    machine.turnOn();
                }else if(msg.operation == 1){
                    machine.shutdown();
                }else if(msg.operation == 2){
                    machine.reboot();
                }
            }
        }
    }
}
