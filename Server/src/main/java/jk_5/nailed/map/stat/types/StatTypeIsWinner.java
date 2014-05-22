package jk_5.nailed.map.stat.types;

import com.google.gson.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeIsWinner implements IStatType {

    @Override
    public void readAdditionalData(JsonObject obj, Stat stat) {
        stat.store("watchingWinnerTeam", obj.has("team") ? obj.get("team").getAsString() : "");
    }

    public void onWinnerSet(Map map, PossibleWinner winner) {
        for(Stat stat : map.getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    PossibleWinner thisWinner = map.getTeamManager().getTeam((String) stat.load("watchingWinnerTeam"));
                    if(thisWinner == winner){
                        stat.enable();
                    }
                    Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername((String) stat.load("watchingWinnerPlayer"));
                    if(p != null && p.getCurrentMap() == map && p == winner){
                        stat.enable();
                    }
                }
            }
        }
    }
}
