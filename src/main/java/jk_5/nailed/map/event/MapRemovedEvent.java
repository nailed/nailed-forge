package jk_5.nailed.map.event;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.map.Map;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class MapRemovedEvent extends Event {

    public final Map map;
}
