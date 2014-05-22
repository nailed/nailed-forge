package jk_5.nailed.api.map.scoreboard;

import javax.annotation.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.scripting.*;

/**
 * This represents an objective in the scoreboard system.
 * An objective is a score type that can be displayed on the client gui
 * The individual scores can be set for either players or other names (this depends on the user of the api)
 *
 * @author jk-5
 */
public interface Objective extends ILuaObject {

    /**
     * Obtain the map that belongs to this Objective.
     *
     * @return The {@link Map} this Objective is valid for.
     */
    @Nonnull
    Map getMap();

    /**
     * Get the unique id for this Objective.
     *
     * @return The unique id for this objective. This id may be chosen upon creation in {@link ScoreboardManager#getOrCreateObjective(String)}.
     */
    @Nonnull
    String getId();

    /**
     * Get the display name for this objective. Note that this may be formatted (color, bold, italic).
     *
     * @return The displayName of this Objective.
     */
    @Nonnull
    String getDisplayName();

    /**
     * Sets the displayName of this Objective.
     * Note that the displayName may not be bigger than 32.
     *
     * @param displayName The new displayName for this objective.
     * @throws IllegalArgumentException When the displayName is longer than 32.
     */
    void setDisplayName(@Nonnull String displayName);

    /**
     * Gets the score object for the specified name, or creating one if it doesn't already exist.
     * Note that the name may not be bigger than 16.
     *
     * @param name The name of the score object. (<= 16).
     * @return The score object.
     * @throws IllegalArgumentException When the name is longer than 16.
     */
    @Nonnull
    Score getScore(@Nonnull String name);

    /**
     * Removes the score from the objective.
     *
     * @param score The score to remove.
     */
    void removeScore(@Nonnull Score score);
}
