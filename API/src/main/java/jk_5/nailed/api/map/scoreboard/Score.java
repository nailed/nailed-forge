package jk_5.nailed.api.map.scoreboard;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Score {

    public String getName();
    public int getValue();
    public void setValue(int value);
    public void addValue(int value);
    public void update();
}
