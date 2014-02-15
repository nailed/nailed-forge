package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.gui.GuiTerminal;
import jk_5.nailed.client.gui.ScriptingManager;
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
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerminal());
        ScriptingManager.currentSynchronizer.requestUpdate();
    }
}
