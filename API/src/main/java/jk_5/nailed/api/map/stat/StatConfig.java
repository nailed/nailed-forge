package jk_5.nailed.api.map.stat;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface StatConfig {

    public StatConfig clone();
    public Stat getStat(String name);
    public List<Stat> getStats();
}
