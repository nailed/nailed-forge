package jk_5.nailed.map.stat.types;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.config.ConfigTag;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.gameloop.InstructionController;
import jk_5.nailed.map.stat.DefaultStat;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeIsWinner implements IStatType {

    @Override
    public void readAdditionalData(ConfigTag config, Stat stat) {
        stat.store("watchingWinnerTeam", config.getTag("team").getValue(""));
    }

    public void onWinnerSet(InstructionController controller, PossibleWinner winner) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    PossibleWinner thisWinner = controller.getMap().getTeamManager().getTeam((String) stat.load("watchingWinnerTeam"));
                    if(thisWinner == winner){
                        stat.enable();
                    }
                    Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername((String) stat.load("watchingWinnerPlayer"));
                    if(p != null && p.getCurrentMap() == controller.getMap() && p == winner){
                        stat.enable();
                    }
                }
            }
        }
    }
}
