package jk_5.nailed.api.map.stat;

import jk_5.nailed.api.config.ConfigTag;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IStatType {

    void readAdditionalData(ConfigTag config, Stat stat);
}