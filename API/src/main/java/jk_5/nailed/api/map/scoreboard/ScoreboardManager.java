package jk_5.nailed.api.map.scoreboard;

import jk_5.nailed.api.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardManager {

    @Nonnull public Objective getOrCreateObjective(@Nonnull String name);
    @Nullable public Objective getObjective(@Nonnull String name);

    public void onPlayerJoinedMap(@Nonnull Player player);
    public void onPlayerLeftMap(@Nonnull Player player);

    /**
     * Displays the given objective at the given location
     * Pass null as the objective to clear the display at that slot
     *
     * @param type The slot to display the objective
     * @param objective The objective to display
     */
    public void setDisplay(@Nonnull DisplayType type, @Nullable Objective objective);

    @Nonnull public ScoreboardTeam getOrCreateTeam(@Nonnull String id);
    @Nullable public ScoreboardTeam getTeam(@Nonnull String id);
}
