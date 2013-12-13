package jk_5.nailed.map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import jk_5.nailed.event.PlayerChangedDimensionEvent;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.Team;
import jk_5.nailed.players.TeamBuilder;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamManager {

    @Getter private final Map map;
    @Getter private final Team defaultTeam;
    @Getter private final List<Team> teams = Lists.newArrayList();

    public TeamManager(Map map){
        this.map = map;
        this.defaultTeam = new Team(this.map, "unknown-" + this.map.getSaveFileName());

        MinecraftForge.EVENT_BUS.register(this);

        if(this.map.getMappack() == null) return;

        for(TeamBuilder builder : this.map.getMappack().getMappackMetadata().getDefaultTeams()){
            Team team = builder.build(this.map);
            System.out.println("Registering team " + team.getName());
            this.teams.add(builder.build(this.map));
        }
    }

    private final BiMap<Player, Team> playerTeamMap = HashBiMap.create();

    @ForgeSubscribe
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event){
        if(event.player.getCurrentMap() != this.map) return;
        if(!this.playerTeamMap.containsKey(event.player)){
            this.playerTeamMap.put(event.player, this.defaultTeam);
            event.player.getEntity().refreshDisplayName();
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
}
