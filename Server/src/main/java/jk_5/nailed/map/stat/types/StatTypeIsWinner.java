package jk_5.nailed.map.stat.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.stat.DefaultStat;

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
