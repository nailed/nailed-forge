package jk_5.nailed.map.event;

import jk_5.nailed.map.Map;
import lombok.RequiredArgsConstructor;
import net.minecraftforge.event.Event;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class MapCreatedEvent extends Event {

    public final Map map;
}
