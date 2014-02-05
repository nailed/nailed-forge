package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.player.Player;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class PlayerChatEvent extends Event {
    public final Player player;
    public final String message;
}
