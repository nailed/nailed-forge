package jk_5.nailed.map.stat.types;

import jk_5.nailed.common.util.config.ConfigTag;
import jk_5.nailed.map.gameloop.InstructionController;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.IStatType;
import jk_5.nailed.map.stat.Stat;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeGameloopPaused implements IStatType {

    @Override
    public void readAdditionalData(ConfigTag config, Stat stat) {
    }

    public void onPause(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.enable();
                }
            }
        }
    }

    public void onResume(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.disable();
                }
            }
        }
    }
}
