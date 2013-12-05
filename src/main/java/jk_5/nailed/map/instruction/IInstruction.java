package jk_5.nailed.map.instruction;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IInstruction {

    void injectArguments(String args);
    IInstruction cloneInstruction();
    void execute(GameController controller);
}
