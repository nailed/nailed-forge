package jk_5.nailed.map.stat.types;

import com.google.gson.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeGameHasWinner implements IStatType {

    @Override
    public void readAdditionalData(JsonObject obj, Stat stat) {

    }

    public void onWin(Map map) {
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
