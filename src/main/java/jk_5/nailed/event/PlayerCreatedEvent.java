package jk_5.nailed.event;

import jk_5.nailed.players.Player;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event;

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
