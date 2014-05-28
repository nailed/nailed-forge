package jk_5.nailed.map;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.entity.player.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.players.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamManager implements jk_5.nailed.api.map.team.TeamManager {

    private final Team defaultTeam;
    private final List<Team> teams = Lists.newArrayList();

    private final java.util.Map<Player, Team> playerTeamMap = Maps.newHashMap();

    public TeamManager(Map map) {
        this.defaultTeam = new TeamUndefined(map);

        if(map.getMappack() == null){
            return;
        }

        for(TeamBuilder builder : map.getMappack().getMappackMetadata().getDefaultTeams()){
            this.teams.add(builder.build(map));
        }
    }

    @Override
    public Team getTeam(String name) {
        for(Team team : this.teams){
            if(team.getTeamId().equals(name)){
                return team;
            }
        }
        return null;
    }

    @Override
    public Team getPlayerTeam(Player player) {
        Team team = this.playerTeamMap.get(player);
        if(team == null){
            return this.defaultTeam;
        }
        return team;
    }

    @Override
    public void setPlayerTeam(Player player, Team team) {
        if(this.playerTeamMap.get(player) == team){
            return;
        }
        if(this.playerTeamMap.containsKey(player)){
            team.onRemovePlayer(player);
            this.playerTeamMap.remove(player);
        }
        this.playerTeamMap.put(player, team);
        team.onAddPlayer(player);
        player.getEntity().refreshDisplayName();
    }

    public void onWorldSet() {
        for(Team team : this.teams){
            team.onWorldSet();
        }
    }

    public void onPlayerJoinedMap(Player player) {
        if(!this.playerTeamMap.containsKey(player)){
            this.playerTeamMap.put(player, this.defaultTeam);
            player.getEntity().refreshDisplayName();
        }
    }

    public void onPlayerLeftMap(Player player) {
        if(!this.playerTeamMap.containsKey(player)){
            this.playerTeamMap.put(player, this.defaultTeam);
            EntityPlayer ent = player.getEntity();
            if(ent != null){
                ent.refreshDisplayName();
            }
        }
    }

    public Team getDefaultTeam() {
        return this.defaultTeam;
    }

    public List<Team> getTeams() {
        return this.teams;
    }
}
