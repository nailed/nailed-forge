package jk_5.nailed.network.handlers;

import io.netty.channel.*;

import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;

import jk_5.nailed.gui.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiReturnDataHandler extends SimpleChannelInboundHandler<NailedPacket.GuiReturnDataPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.GuiReturnDataPacket msg) throws Exception {
        EntityPlayerMP player = NailedNetworkHandler.getPlayer(ctx);
        TileEntity tile = player.worldObj.getTileEntity(msg.x, msg.y, msg.z);
        if(tile != null && tile instanceof IGuiReturnHandler){
            ((IGuiReturnHandler) tile).readGuiCloseData(msg.data);
        }
    }
}
