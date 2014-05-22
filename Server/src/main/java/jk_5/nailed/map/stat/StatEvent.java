package jk_5.nailed.map.stat;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatEvent extends Event {

    public final Stat stat;

    public StatEvent(Stat stat) {
        this.stat = stat;
    }

    public static class Enable extends StatEvent {

        public Enable(Stat stat) {
            super(stat);
        }
    }

    public static class Disable extends StatEvent {

        public Disable(Stat stat) {
            super(stat);
        }
    }
}
