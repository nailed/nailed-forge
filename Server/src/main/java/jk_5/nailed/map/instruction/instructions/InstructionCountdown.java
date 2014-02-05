package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.TimedInstruction;
import jk_5.nailed.util.Utils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionCountdown extends TimedInstruction {

    private int ticks = 0;
    private String message = null;

    @Override
    public void injectArguments(String arguments) {
        String args[] = arguments.split(" ", 2);
        this.message = args[1];
        String timedata = args[0].toLowerCase().trim();
        if (timedata.endsWith("sec")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 3));
        else if (timedata.endsWith("min")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 3)) * 60;
        else if (timedata.endsWith("hour")) this.ticks = Integer.parseInt(timedata.substring(0, timedata.length() - 4)) * 3600;
        else throw new RuntimeException("Unable to parse the countdown command properly");
    }

    @Override
    public boolean executeTimed(GameController controller, int ticks) {
        controller.broadcastTimeRemaining(String.format(this.message, Utils.secondsToShortTimeString(this.ticks - ticks)));
        //if ((this.ticks - ticks) == 60 || (this.ticks - ticks) == 30 || (this.ticks - ticks) == 20 || (this.ticks - ticks) == 10 || (this.ticks - ticks) <= 5 || ticks % 60 == 0) {
           //if ((this.ticks - ticks) <= 5) {
            //    controller.getMap.getPlayers.foreach(_.playSound("note.harp", 1.5f, if ((this.ticks - ticks) == 0) 2 else 1))
            //}
        //}
        return ticks >= this.ticks;
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionCountdown(this.ticks, this.message);
    }
}
