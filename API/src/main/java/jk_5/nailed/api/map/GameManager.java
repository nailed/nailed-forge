package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface GameManager {

    public void setWatchUnready(boolean watchUnready);
    public boolean isWatchUnready();
    public void setWinnerInterrupt(boolean winnerInterrupt);
    public boolean isWinnerInterrupt();
    public void setCountdownMessage(String message);
    public void setWinner(PossibleWinner winner);
    public void startGame();
    public void stopGame();
}
