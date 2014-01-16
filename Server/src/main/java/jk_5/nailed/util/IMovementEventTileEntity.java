package jk_5.nailed.util;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMovementEventTileEntity {

    void onMovementEvent(int type, EntityPlayerMP player);
}
