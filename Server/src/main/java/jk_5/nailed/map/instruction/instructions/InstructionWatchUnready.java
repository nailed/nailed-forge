package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class InstructionWatchUnready implements IInstruction {

    @Override
    public void injectArguments(String args) {

    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionWatchUnready();
    }

    @Override
    public void execute(GameController controller) {
        controller.save("watchunready", true);
    }
}
