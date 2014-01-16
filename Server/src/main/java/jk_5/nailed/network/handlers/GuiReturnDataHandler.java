package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.gui.IGuiReturnHandler;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiReturnDataHandler extends SimpleChannelInboundHandler<NailedPacket.GuiReturnDataPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.GuiReturnDataPacket msg) throws Exception{
        EntityPlayerMP player = NailedNetworkHandler.getPlayer(ctx);
        TileEntity tile = player.worldObj.func_147438_o(msg.x, msg.y, msg.z);
        if(tile != null && tile instanceof IGuiReturnHandler){
            ((IGuiReturnHandler) tile).readGuiCloseData(msg.data);
        }
    }
}
