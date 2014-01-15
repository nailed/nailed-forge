package jk_5.nailed.map.stat.types;

import jk_5.nailed.common.util.config.ConfigTag;
import jk_5.nailed.map.stat.IStatType;
import jk_5.nailed.map.stat.Stat;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeModifiable implements IStatType {

    @Override
    public void readAdditionalData(ConfigTag config, Stat stat){
        stat.setDefaultState(config.getTag("default").getBooleanValue(false));
    }
}
