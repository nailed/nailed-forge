package jk_5.nailed.map.stat.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.map.stat.DefaultStat;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeGameloopStopped implements IStatType {

    @Override
    public void readAdditionalData(JsonObject obj, Stat stat) {

    }

    public void onEnd(Map map) {
        for(Stat stat : map.getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.enable();
                }
            }
        }
    }

    public void reset(Map map) {
        for(Stat stat : map.getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.disable();
                }
            }
        }
    }
}
