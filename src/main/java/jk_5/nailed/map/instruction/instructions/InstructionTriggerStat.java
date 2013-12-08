package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
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
        controller.getMap().getStatManager().getStat(this.stat);//.trigger();
    }
}
