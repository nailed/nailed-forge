package jk_5.nailed.api.map.scoreboard;

import jk_5.nailed.api.player.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardManager {

    public Objective getOrCreateObjective(String name);
    public Objective getObjective(String name);

    public void onPlayerJoinedMap(Player player);
    public void onPlayerLeftMap(Player player);

    /**
     * Displays the given objective at the given location
     * Pass null as the objective to clear the display at that slot
     *
     * @param type The slot to display the objective
     * @param objective The objective to display
     */
    public void setDisplay(DisplayType type, Objective objective);

    public ScoreboardTeam getOrCreateTeam(String id);
    public ScoreboardTeam getTeam(String id);
}
