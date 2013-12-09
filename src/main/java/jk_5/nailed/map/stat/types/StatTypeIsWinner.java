package jk_5.nailed.map.stat.types;

import jk_5.nailed.map.gameloop.InstructionController;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.IStatType;
import jk_5.nailed.map.stat.Stat;
import jk_5.nailed.players.Team;
import jk_5.nailed.util.config.ConfigTag;

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

    public void onWinnerSet(InstructionController controller, Team winner) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    Team team = controller.getMap().getTeamManager().getTeam((String) stat.load("watchingWinnerTeam"));
                    if(team == winner){
                        stat.enable();
                    }
                }
            }
        }
    }
}
