package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IInstruction {

    void injectArguments(String args);

    /**
     * Note: The settings of the cloned instruction should be identical to the original instruction
     * @return a copy of the instruction
     */
    IInstruction cloneInstruction();
    void execute(GameController controller);
}
