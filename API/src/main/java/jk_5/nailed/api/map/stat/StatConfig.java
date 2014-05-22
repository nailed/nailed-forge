package jk_5.nailed.api.map.stat;

import java.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface StatConfig {

    StatConfig copy();
    Stat getStat(String name);
    List<Stat> getStats();
}
