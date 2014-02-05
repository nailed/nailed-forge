package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.map.Map;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class MapCreatedEvent extends Event {
    public final Map map;
}
