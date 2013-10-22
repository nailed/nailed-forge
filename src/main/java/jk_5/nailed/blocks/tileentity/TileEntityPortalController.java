package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.map.TeleportOptions;
import lombok.Getter;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityPortalController extends TileEntity {

    public short yaw;
    public short pitch;
    @Getter private int color;

    public TileEntityPortalController(){
        this.tileEntityInvalid = false;
        this.yaw = 0;
        this.pitch = 0;
        this.color = 3355647;
    }

    public TeleportOptions getDestination(){
        return null;
    }
}
