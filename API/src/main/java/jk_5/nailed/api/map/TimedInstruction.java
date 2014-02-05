package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class TimedInstruction implements IInstruction {

    /**
     * Will be executed until this method returns true
     *
     * @param controller The gamecontroller that you can use to interact with the map
     * @param ticks How long we are waiting on this task already
     * @return true if you are done executing, false otherwise.
     */
    public abstract boolean executeTimed(GameController controller, int ticks);

    @Override
    public final void execute(GameController controller) {
        //NOOP. Use the timed version above
    }
}
