package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.blocks.tileentity.IGuiTileEntity;
import jk_5.nailed.client.gui.NailedGui;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class OpenGuiHandler extends SimpleChannelInboundHandler<NailedPacket.GuiOpen> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.GuiOpen msg) throws Exception{
        TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(msg.x, msg.y, msg.z);
        if(tile != null && tile instanceof IGuiTileEntity){
            NailedGui gui = ((IGuiTileEntity) tile).getGui();
            if(gui == null) return;
            gui = gui.readGuiData(msg.data);
            if(gui == null) return;
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }
}
