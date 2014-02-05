package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.types.StatTypeModifiable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionTriggerStat implements IInstruction {

    private String stat;

    @Override
    public void injectArguments(String args) {
        this.stat = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionTriggerStat(this.stat);
    }

    @Override
    public void execute(GameController controller) {
        Stat stat = controller.getMap().getStatManager().getStat(this.stat);
        if(stat != null && stat instanceof DefaultStat){
            IStatType type = ((DefaultStat) stat).getType();
            if(type instanceof StatTypeModifiable){
                //stat.trigger();
            }
        }
    }
}
