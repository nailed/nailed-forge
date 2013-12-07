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
public class InstructionSetWinner implements IInstruction {

    private String winner;

    @Override
    public void injectArguments(String args) {
        this.winner = args;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetWinner(this.winner);
    }

    @Override
    public void execute(GameController controller) {
        controller.setWinner(controller.getMap().getTeamManager().getTeam(this.winner));
    }
}
