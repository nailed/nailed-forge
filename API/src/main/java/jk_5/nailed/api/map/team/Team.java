package jk_5.nailed.api.map.team;

import jk_5.nailed.api.ChatColor;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.Spawnpoint;
import jk_5.nailed.api.player.Player;
import net.minecraft.scoreboard.ScorePlayerTeam;

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
    public ScorePlayerTeam getScoreboardTeam();
    public void setScoreboardTeam(ScorePlayerTeam team);
    public Spawnpoint getSpawnpoint();
    public void setSpawnpoint(Spawnpoint spawnpoint);
    public String getColoredName();

    public void onAddPlayer(Player player);
    public void onRemovePlayer(Player player);
    public void onWorldSet();
    public void addPlayerToScoreboardTeam(Player player);
    public void removePlayerFromScoreboardTeam(Player player);

    public boolean shouldOverrideDefaultSpawnpoint();

    public List<Player> getMembers();
}
