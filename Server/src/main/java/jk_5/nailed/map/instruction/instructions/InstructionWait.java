package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.TimedInstruction;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionWait extends TimedInstruction {

    private int ticks = 0;

    @Override
    public void injectArguments(String args) {
        String timedata = args.toLowerCase().trim();
        if (timedata.endsWith("sec")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 3));
        else if (timedata.endsWith("min")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 3)) * 60;
        else if (timedata.endsWith("hour")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 4)) * 3600;
        else throw new RuntimeException("Unable to parse the countdown command properly");
    }

    @Override
    public boolean executeTimed(GameController controller, int ticks) {
        return ticks >= this.ticks;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionWait(this.ticks);
    }
}
