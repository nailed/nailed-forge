package jk_5.nailed.event;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.players.Player;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class PlayerLeaveEvent extends Event {
    public final Player player;
}
