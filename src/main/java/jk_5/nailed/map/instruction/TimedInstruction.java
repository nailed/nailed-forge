package jk_5.nailed.map.instruction;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class TimedInstruction implements IInstruction {

    public abstract boolean executeTimed(GameController controller, int ticks);
}
