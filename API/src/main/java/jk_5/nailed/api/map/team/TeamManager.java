package jk_5.nailed.api.map.team;

import jk_5.nailed.api.player.Player;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface TeamManager {

    public Team getTeam(String team);
    public Team getPlayerTeam(Player player);
    public void setPlayerTeam(Player player, Team team);
    public List<Team> getTeams();
}
