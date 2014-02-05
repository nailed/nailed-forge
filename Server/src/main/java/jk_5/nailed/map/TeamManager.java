package jk_5.nailed.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jk_5.nailed.NailedServer;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.map.team.TeamBuilder;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.players.NailedTeam;
import jk_5.nailed.players.TeamUndefined;
import lombok.Getter;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamManager implements jk_5.nailed.api.map.team.TeamManager {

    @Getter private final Team defaultTeam;
    @Getter private final List<Team> teams = Lists.newArrayList();

    private final java.util.Map<Player, Team> playerTeamMap = Maps.newHashMap();

    public TeamManager(Map map){
        this.defaultTeam = new TeamUndefined(map);

        if(map.getMappack() == null) return;

        for(TeamBuilder builder : map.getMappack().getMappackMetadata().getDefaultTeams()){
            this.teams.add(builder.build(map));
        }
    }

    public Team getTeam(String name){
        for(Team team : this.teams){
            if(team.getTeamId().equals(name)){
                return team;
            }
        }
        return null;
    }

    public Team getPlayerTeam(Player player) {
        Team team = this.playerTeamMap.get(player);
        if(team == null) return this.defaultTeam;
        return team;
    }

    public void setPlayerTeam(Player player, Team team) {
        if(this.playerTeamMap.get(player) == team) return;
        if(this.playerTeamMap.containsKey(player)){
            team.onRemovePlayer(player);
            this.playerTeamMap.remove(player);
        }
        this.playerTeamMap.put(player, team);
        team.onAddPlayer(player);
        player.getEntity().refreshDisplayName();
    }

    public void onWorldSet(){
        for(Team team : this.teams){
            team.onWorldSet();
        }
    }

    public void onPlayerJoinedMap(Player player){
        if(!this.playerTeamMap.containsKey(player)){
            this.playerTeamMap.put(player, this.defaultTeam);
            player.getEntity().refreshDisplayName();
        }
        Team team = this.playerTeamMap.get(player);
        if(team.getScoreboardTeam() != null){
            //player.sendPacket(new Packet209SetPlayerTeam(team.getScoreboardTeam(), 0));
        }
        team.addPlayerToScoreboardTeam(player);
    }

    public void onPlayerLeftMap(Player player){
        if(!this.playerTeamMap.containsKey(player)){
            this.playerTeamMap.put(player, this.defaultTeam);
            player.getEntity().refreshDisplayName();
        }
        Team team = this.playerTeamMap.get(player);
        team.removePlayerFromScoreboardTeam(player);
        if(team.getScoreboardTeam() != null){
            //player.sendPacket(new Packet209SetPlayerTeam(team.getScoreboardTeam(), 1));
        }
    }

    public void onGameStarted(){
        for(Team team : this.teams){
            if(!(team instanceof NailedTeam)) continue;
            NailedTeam t = (NailedTeam) team;
            if(t.getTeamSpeakChannelID() == -1){
                NailedServer.getTeamspeakClient().createChannelFor(t);
            }
            NailedServer.getTeamspeakClient().movePlayersIntoChannel(t);
        }
    }

    public void onGameEnded(){
        for(Team team : this.teams){
            if(!(team instanceof NailedTeam)) continue;
            NailedTeam t = (NailedTeam) team;
            if(t.getTeamSpeakChannelID() != -1){
                NailedServer.getTeamspeakClient().movePlayersToLobby(t);
                NailedServer.getTeamspeakClient().removeChannel(t);
            }
        }
    }
}
