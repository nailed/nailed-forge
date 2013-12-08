package jk_5.nailed.map.stat;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Stat {

    void enable();
    void disable();
    boolean isEnabled();
    String getName();
    Stat clone();
}
