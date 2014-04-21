package jk_5.nailed.api.map.team;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;
import jk_5.nailed.util.ChatColor;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Team extends PossibleWinner {

    public Map getMap();
    public String getTeamId();

    public String getName();
    public void setName(String name);
    public ChatColor getColor();
    public void setColor(ChatColor color);
    public Player getLeader();
    public void setLeader(Player player);
    public boolean isReady();
    public void setReady(boolean ready);
    public boolean isFriendlyFireEnabled();
    public void setFriendlyFireEnabled(boolean enabled);
    public boolean canSeeFriendlyInvisibles();
    public void setSeeFriendlyInvisibles(boolean enabled);
    public ScoreboardTeam getScoreboardTeam();
    public void setScoreboardTeam(ScoreboardTeam team);
    public Location getSpawnpoint();
    public void setSpawnpoint(Location spawnpoint);
    public String getColoredName();

    public void onAddPlayer(Player player);
    public void onRemovePlayer(Player player);
    public void onWorldSet();
    public void addPlayerToScoreboardTeam(Player player);
    public void removePlayerFromScoreboardTeam(Player player);

    public boolean shouldOverrideDefaultSpawnpoint();

    public List<Player> getMembers();
}
