package jk_5.nailed.gui.container;

import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * No description given
 *
 * @author jk-5
 */
public class ContainerStatEmitter extends Container {

    public ContainerStatEmitter(TileEntityStatEmitter tile) {

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        Player player = PlayerRegistry.instance().getPlayer(entityPlayer.username);
        return player != null && player.isOp();
    }
}
