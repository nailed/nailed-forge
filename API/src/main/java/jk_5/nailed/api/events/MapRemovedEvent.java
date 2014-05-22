package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class MapRemovedEvent extends Event {

    public final Map map;
}
