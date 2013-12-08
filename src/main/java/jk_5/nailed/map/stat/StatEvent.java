package jk_5.nailed.map.stat;

import lombok.RequiredArgsConstructor;
import net.minecraftforge.event.Event;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class StatEvent extends Event {

    public final Stat stat;

    public static class Enable extends StatEvent {
        public Enable(Stat stat){
            super(stat);
        }
    }

    public static class Disable extends StatEvent {
        public Disable(Stat stat){
            super(stat);
        }
    }
}
