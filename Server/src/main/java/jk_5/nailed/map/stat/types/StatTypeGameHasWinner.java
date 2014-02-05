package jk_5.nailed.map.stat.types;

import jk_5.nailed.api.config.ConfigTag;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.map.gameloop.InstructionController;
import jk_5.nailed.map.stat.DefaultStat;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeGameHasWinner implements IStatType {

    @Override
    public void readAdditionalData(ConfigTag config, Stat stat) {
    }

    public void onWin(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.enable();
                }
            }
        }
    }

    public void reset(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.disable();
                }
            }
        }
    }
}
