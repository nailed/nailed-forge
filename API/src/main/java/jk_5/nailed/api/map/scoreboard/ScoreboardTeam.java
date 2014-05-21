package jk_5.nailed.api.map.scoreboard;

import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.ILuaObject;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardTeam extends ILuaObject {

    @Nonnull public String getId();
    @Nonnull public String getDisplayName();
    public void setDisplayName(@Nonnull String displayName);
    @Nonnull public String getPrefix();
    public void setPrefix(@Nonnull String prefix);
    @Nonnull public String getSuffix();
    public void setSuffix(@Nonnull String suffix);
    public boolean isFriendlyFire();
    public void setFriendlyFire(boolean friendlyFire);
    public boolean isFriendlyInvisiblesVisible();
    public void setFriendlyInvisiblesVisible(boolean friendlyInvisiblesVisible);

    public boolean addPlayer(@Nonnull Player player);
    public boolean removePlayer(@Nonnull Player player);
    @Nonnull public Set<Player> getPlayers();
    @Nonnull public Set<String> getPlayerNames();
}
