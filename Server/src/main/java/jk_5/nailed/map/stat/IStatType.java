package jk_5.nailed.map.stat;

import jk_5.nailed.common.util.config.ConfigTag;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IStatType {

    void readAdditionalData(ConfigTag config, Stat stat);
}
