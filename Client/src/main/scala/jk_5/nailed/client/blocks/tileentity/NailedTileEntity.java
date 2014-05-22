package jk_5.nailed.client.blocks.tileentity;

import io.netty.buffer.*;

import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public abstract class NailedTileEntity extends TileEntity {

    public boolean onBlockActivated(EntityPlayer entity, int side, float hitX, float hitY, float hitZ){
        if(this instanceof IGuiTileEntity){
            if(this.worldObj.isRemote){
                return true;
            }
        }
        return false;
    }

    public void readData(ByteBuf buffer){

    }
}
