package cpw.mods.fml.common.gameevent;

/**
 * No description given
 *
 * @author jk-5
 */
public class TickEvent {

    public static enum Phase {
        START, END
    }

    public static class RenderTickEvent {
        public Phase phase;
    }
}
