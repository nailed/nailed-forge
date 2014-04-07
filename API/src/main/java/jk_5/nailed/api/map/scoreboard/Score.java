package jk_5.nailed.api.map.scoreboard;

import javax.annotation.Nonnull;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Score {

    @Nonnull public String getName();
    public int getValue();
    public void setValue(int value);
    public void addValue(int value);
    public void update();
}
