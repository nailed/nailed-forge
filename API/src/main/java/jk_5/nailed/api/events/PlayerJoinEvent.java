package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.player.Player;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@Deprecated
@RequiredArgsConstructor
public class PlayerJoinEvent extends Event {
    public final Player player;
}
