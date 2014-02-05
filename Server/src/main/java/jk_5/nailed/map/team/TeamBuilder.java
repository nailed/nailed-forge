package jk_5.nailed.map.team;

import jk_5.nailed.api.ChatColor;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.players.NailedTeam;
import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
@Getter
@Setter
public class TeamBuilder implements jk_5.nailed.api.map.team.TeamBuilder {

    private String name;
    private String internalName;
    private ChatColor color;
    private boolean friendlyFire;
    private boolean seeFriendlyInvisibles;

    public Team build(Map map){
        Team team = new NailedTeam(map, this.internalName);
        team.setName(this.name);
        team.setColor(this.color);
        team.setFriendlyFireEnabled(this.friendlyFire);
        team.setSeeFriendlyInvisibles(this.seeFriendlyInvisibles);
        return team;
    }
}
