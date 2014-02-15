package jk_5.nailed.client.scripting;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.gui.ScriptingManager;
import jk_5.nailed.map.script.ScriptPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class ScriptPacketHandler<T> extends SimpleChannelInboundHandler<T> {

    public static class TerminalDeletedHandler extends ScriptPacketHandler<ScriptPacket.RemoveTerminal> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.RemoveTerminal msg) throws Exception{
            if(ScriptingManager.currentSynchronizer != null){
                ScriptingManager.currentSynchronizer.delete();
            }
        }
    }

    public static class TerminalUpdateHandler extends ScriptPacketHandler<ScriptPacket.UpdateTerminal> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ScriptPacket.UpdateTerminal msg) throws Exception{
            if(ScriptingManager.currentSynchronizer != null){
                ScriptingManager.currentSynchronizer.resize(msg.width, msg.height);
                synchronized(ScriptingManager.currentSynchronizer.terminal){
                    for(int y = 0; y < msg.height; y++){
                        boolean changed;
                        if(y < 30){
                            changed = (msg.lineChangeMask & 1 << y + 1) > 0;
                        }else if(y < 60){
                            changed = (msg.lineChangeMask1 & 1 << y - 30) > 0;
                        }else{
                            changed = (msg.lineChangeMask2 & 1 << y - 30) > 0;
                        }
                        if(changed){
                            ScriptingManager.currentSynchronizer.terminal.setLine(y, ByteBufUtils.readUTF8String(msg.data), ScriptingManager.currentSynchronizer.terminal.getColorLine(y));
                        }
                    }
                    for(int y = 0; y < msg.height; y++){
                        boolean changed;
                        if(y < 30){
                            changed = (msg.colorLineChangeMask & 1 << y + 1) > 0;
                        }else if(y < 60){
                            changed = (msg.colorLineChangeMask1 & 1 << y - 30) > 0;
                        }else{
                            changed = (msg.colorLineChangeMask2 & 1 << y - 30) > 0;
                        }
                        if(changed){
                            ScriptingManager.currentSynchronizer.terminal.setLine(y, ScriptingManager.currentSynchronizer.terminal.getLine(y), ByteBufUtils.readUTF8String(msg.data));
                        }
                    }
                    ScriptingManager.currentSynchronizer.terminal.setCursorPos(msg.cursorX, msg.cursorY);
                    ScriptingManager.currentSynchronizer.terminal.setCursorBlink((msg.lineChangeMask & 0x1) > 0);
                    ScriptingManager.currentSynchronizer.terminal.setTextColor(msg.colors & 0xF);
                    ScriptingManager.currentSynchronizer.terminal.setBackgroundColor(msg.colors & 0xF0 >> 4);
                }
            }
        }
    }
}
