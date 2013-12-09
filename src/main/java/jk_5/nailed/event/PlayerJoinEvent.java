package jk_5.nailed.event;

import jk_5.nailed.players.Player;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.event.Event;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class PlayerJoinEvent extends Event {
    public final Player player;
}
