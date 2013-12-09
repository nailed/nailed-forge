package jk_5.nailed.blocks.tileentity;

import codechicken.lib.vec.BlockCoord;
import jk_5.nailed.network.Packets;
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
            if(worldObj.isRemote) return true;
            else{
                Player player = PlayerRegistry.instance().getPlayer(entity.username);
                if(player == null) return true;
                if(tile.canPlayerOpenGui(player)){
                    Packets.OPEN_GUI.newPacket().writeCoord(this.getCoordinates()).sendToPlayer(entity);
                }
                return true;
            }
        }
        return false;
    }

    public BlockCoord getCoordinates(){
        return new BlockCoord(this.xCoord, this.yCoord, this.zCoord);
    }
}
