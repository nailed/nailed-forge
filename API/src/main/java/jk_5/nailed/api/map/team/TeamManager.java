package jk_5.nailed.api.map.team;

import java.util.*;

import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface TeamManager {

    Team getTeam(String team);
    Team getPlayerTeam(Player player);
    void setPlayerTeam(Player player, Team team);
    List<Team> getTeams();
}
