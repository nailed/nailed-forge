package jk_5.nailed.api.map.scoreboard;

import java.util.*;
import javax.annotation.*;

import jk_5.nailed.api.player.*;
import jk_5.nailed.api.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardTeam extends ILuaObject {

    @Nonnull
    String getId();
    @Nonnull
    String getDisplayName();
    void setDisplayName(@Nonnull String displayName);
    @Nonnull
    String getPrefix();
    void setPrefix(@Nonnull String prefix);
    @Nonnull
    String getSuffix();
    void setSuffix(@Nonnull String suffix);
    boolean isFriendlyFire();
    void setFriendlyFire(boolean friendlyFire);
    boolean isFriendlyInvisiblesVisible();
    void setFriendlyInvisiblesVisible(boolean friendlyInvisiblesVisible);

    boolean addPlayer(@Nonnull Player player);
    boolean removePlayer(@Nonnull Player player);
    @Nonnull
    Set<Player> getPlayers();
    @Nonnull
    Set<String> getPlayerNames();
}
