package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.NailedClient;
import jk_5.nailed.client.gui.GuiTerminal;
import jk_5.nailed.client.scripting.ClientMachine;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.client.Minecraft;

/**
 * No description given
 *
 * @author jk-5
 */
public class TerminalGuiHandler extends SimpleChannelInboundHandler<NailedPacket.OpenTerminalGui> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.OpenTerminalGui msg) throws Exception{
        ClientMachine machine = this.getMachine(msg.instanceId);
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerminal(machine, msg.width, msg.height));
        machine.turnOn();
    }

    private ClientMachine getMachine(int id){
        ClientMachine ret = NailedClient.machines().get(id);
        if(ret == null){
            ret = new ClientMachine(id);
            NailedClient.machines().add(id, ret);
        }
        return ret;
    }
}
