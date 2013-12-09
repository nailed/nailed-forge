package jk_5.nailed.map.stat;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Stat {

    void setDefaultState(boolean defaultState);
    void enable();
    void disable();
    boolean isEnabled();
    String getName();
    Stat clone();

    void store(String key, Object value);
    Object load(String key);
}
