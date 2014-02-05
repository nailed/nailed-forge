package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionSetTime implements IInstruction {

    private int time;

    @Override
    public void injectArguments(String args) {
        this.time = Integer.parseInt(args);
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetTime(this.time);
    }

    @Override
    public void execute(GameController controller) {
        controller.getMap().getWorld().setWorldTime(this.time % 24000);
    }
}
