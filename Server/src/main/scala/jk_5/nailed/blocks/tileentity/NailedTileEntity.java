package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.*;

import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.tileentity.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.network.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedTileEntity extends TileEntity {

    public boolean onBlockActivated(EntityPlayer entity, int side, float hitX, float hitY, float hitZ) {
        if(this instanceof IGuiTileEntity){
            IGuiTileEntity tile = (IGuiTileEntity) this;
            if(this.worldObj.isRemote){
                return true;
            }else{
                Player player = NailedAPI.getPlayerRegistry().getPlayer(entity);
                if(player == null){
                    return true;
                }
                if(tile.canPlayerOpenGui(player)){
                    ByteBuf data = Unpooled.buffer();
                    tile.writeGuiData(data);
                    NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.GuiOpen(this.xCoord, this.yCoord, this.zCoord, data), entity);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket() {
        if(!(this instanceof ISynchronizedTileEntity)){
            return null;
        }
        ByteBuf buffer = Unpooled.buffer();
        ((ISynchronizedTileEntity) this).writeData(buffer);
        if(buffer.readableBytes() == 0){
            buffer.release();
            return null;
        }
        return NailedNetworkHandler.getProxyPacket(new NailedPacket.TileEntityData(this.xCoord, this.yCoord, this.zCoord, buffer));
    }
}
