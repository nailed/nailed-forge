package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.ISynchronizedTileEntity;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public abstract class NailedTileEntity extends TileEntity {

    public boolean onBlockActivated(EntityPlayer entity, int side, float hitX, float hitY, float hitZ){
        if(this instanceof IGuiTileEntity){
            IGuiTileEntity tile = (IGuiTileEntity) this;
            if(this.worldObj.isRemote) return true;
            else{
                Player player = NailedAPI.getPlayerRegistry().getPlayer(entity);
                if(player == null) return true;
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
    public Packet getDescriptionPacket(){
        if(!(this instanceof ISynchronizedTileEntity)) return null;
        ByteBuf buffer = Unpooled.buffer();
        ((ISynchronizedTileEntity) this).writeData(buffer);
        if(buffer.readableBytes() == 0){
            buffer.release();
            return null;
        }
        return NailedNetworkHandler.getProxyPacket(new NailedPacket.TileEntityData(this.xCoord, this.yCoord, this.zCoord, buffer));
    }
}
