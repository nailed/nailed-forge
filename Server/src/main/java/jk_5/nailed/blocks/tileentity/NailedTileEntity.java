package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.common.NailedLog;
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
                NailedLog.info("IMPLEMENT ME IN NailedTileEntity!");
                //TODO: Packet
                //if(tile.canPlayerOpenGui(player)){
                //    Packets.OPEN_GUI.newPacket().writeCoord(this.getCoordinates()).sendToPlayer(entity);
                //}
                return true;
            }
        }
        return false;
    }
}
