package jk_5.nailed.api.map.team;

import java.util.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Team extends PossibleWinner, ILuaObject {

    Map getMap();
    String getTeamId();

    String getName();
    void setName(String name);
    ChatColor getColor();
    void setColor(ChatColor color);
    Player getLeader();
    void setLeader(Player player);
    boolean isReady();
    void setReady(boolean ready);
    boolean isFriendlyFireEnabled();
    void setFriendlyFireEnabled(boolean enabled);
    boolean canSeeFriendlyInvisibles();
    void setSeeFriendlyInvisibles(boolean enabled);
    ScoreboardTeam getScoreboardTeam();
    void setScoreboardTeam(ScoreboardTeam team);
    Location getSpawnpoint();
    void setSpawnpoint(Location spawnpoint);
    String getColoredName();

    void onAddPlayer(Player player);
    void onRemovePlayer(Player player);
    void onWorldSet();
    void addPlayerToScoreboardTeam(Player player);
    void removePlayerFromScoreboardTeam(Player player);

    boolean shouldOverrideDefaultSpawnpoint();

    List<Player> getMembers();
    List<String> getMemberNames();
    List<String> getPrefixNames();
}
