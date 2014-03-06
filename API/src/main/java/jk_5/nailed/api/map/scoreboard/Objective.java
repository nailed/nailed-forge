package jk_5.nailed.api.map.scoreboard;

import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Objective {

    public Map getMap();
    public String getId();
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public Score getScore(String name);
    public void removeScore(Score score);
}
