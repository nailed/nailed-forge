package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.player.Player;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class PlayerCreatedEvent extends Event {
    public final EntityPlayer entityPlayer;
    public final Player player;
}
