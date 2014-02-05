package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface InstructionController {

    public Map getMap();
    public boolean isRunning();
    public boolean isPaused();
    public PossibleWinner getWinner();
    public void setWinner(PossibleWinner team);
    public InstructionList getInstructions();

    public void startGame();
    public void stopGame();

    public Object load(String key);
    public void save(String key, Object value);
}
