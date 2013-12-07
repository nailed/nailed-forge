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
public class InstructionStopWinnerinterrupt implements IInstruction {

    @Override
    public void injectArguments(String args) {

    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionStopWinnerinterrupt();
    }

    @Override
    public void execute(GameController controller) {
        controller.save("interruptOnWin", false);
    }
}
