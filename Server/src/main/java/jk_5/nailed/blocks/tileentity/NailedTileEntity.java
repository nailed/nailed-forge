package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
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
            if(this.field_145850_b.isRemote) return true;
            else{
                Player player = PlayerRegistry.instance().getPlayer(entity);
                if(player == null) return true;
                if(tile.canPlayerOpenGui(player)){
                    ByteBuf data = Unpooled.buffer();
                    tile.writeGuiData(data);
                    NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.GuiOpen(this.field_145851_c, this.field_145848_d, this.field_145849_e, data), entity);
                }
                return true;
            }
        }
        return false;
    }
}
