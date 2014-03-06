package jk_5.nailed.api.map.scoreboard;

import jk_5.nailed.api.player.Player;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ScoreboardTeam {

    public String getId();
    public String getDisplayName();
    public void setDisplayName(String displayName);
    public String getPrefix();
    public void setPrefix(String prefix);
    public String getSuffix();
    public void setSuffix(String suffix);
    public boolean isFriendlyFire();
    public void setFriendlyFire(boolean friendlyFire);
    public boolean isFriendlyInvisiblesVisible();
    public void setFriendlyInvisiblesVisible(boolean friendlyInvisiblesVisible);

    public boolean addPlayer(Player player);
    public boolean removePlayer(Player player);
    public Set<Player> getPlayers();
    public Set<String> getPlayerNames();
}
