package jk_5.nailed.map.stat.types;

import jk_5.nailed.map.stat.IStatType;
import jk_5.nailed.map.stat.Stat;
import jk_5.nailed.util.config.ConfigTag;

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
