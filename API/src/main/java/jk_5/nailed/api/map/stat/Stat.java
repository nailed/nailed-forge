package jk_5.nailed.api.map.stat;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Stat extends Cloneable {

    void setDefaultState(boolean defaultState);
    void enable();
    void disable();
    boolean isEnabled();
    String getName();
    Stat copy();

    void store(String key, Object value);
    Object load(String key);
}
