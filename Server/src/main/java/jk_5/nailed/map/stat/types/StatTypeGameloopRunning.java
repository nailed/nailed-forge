package jk_5.nailed.map.stat.types;

import jk_5.nailed.map.gameloop.InstructionController;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.IStatType;
import jk_5.nailed.map.stat.Stat;
import jk_5.nailed.util.config.ConfigTag;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeGameloopRunning implements IStatType {

    @Override
    public void readAdditionalData(ConfigTag config, Stat stat) {
    }

    public void onStart(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.enable();
                }
            }
        }
    }

    public void onEnd(InstructionController controller) {
        for(Stat stat : controller.getMap().getStatManager().getStats().getStats()){
            if(stat instanceof DefaultStat){
                if(((DefaultStat) stat).getType() == this){
                    stat.disable();
                }
            }
        }
    }
}
