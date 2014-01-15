package jk_5.nailed.client.blocks.tileentity;

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
            if(this.field_145850_b.isRemote) return true;
        }
        return false;
    }
}
