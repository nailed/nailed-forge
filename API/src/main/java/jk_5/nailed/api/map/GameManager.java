package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface GameManager {

    void setWatchUnready(boolean watchUnready);
    boolean isWatchUnready();
    void setWinnerInterrupt(boolean winnerInterrupt);
    boolean isWinnerInterrupt();
    void setCountdownMessage(String message);
    void setWinner(PossibleWinner winner);
    void startGame();
    void stopGame();
    boolean isGameRunning();

    void onStarted();
    void onStopped(boolean finished);
}
