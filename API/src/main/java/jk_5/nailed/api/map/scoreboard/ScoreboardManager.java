package jk_5.nailed.api.map.scoreboard;

import javax.annotation.*;

import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardManager {

    @Nonnull
    Objective getOrCreateObjective(@Nonnull String name);
    @Nullable
    Objective getObjective(@Nonnull String name);

    void onPlayerJoinedMap(@Nonnull Player player);
    void onPlayerLeftMap(@Nonnull Player player);

    /**
     * Displays the given objective at the given location
     * Pass null as the objective to clear the display at that slot
     *
     * @param type      The slot to display the objective
     * @param objective The objective to display
     */
    void setDisplay(@Nonnull DisplayType type, @Nullable Objective objective);

    @Nonnull
    ScoreboardTeam getOrCreateTeam(@Nonnull String id);
    @Nullable
    ScoreboardTeam getTeam(@Nonnull String id);
}
