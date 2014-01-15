package jk_5.nailed.players;

import jk_5.nailed.common.util.ChatColor;
import jk_5.nailed.map.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamBuilder {

    private String name;
    private String internalName;
    private ChatColor color;
    private boolean friendlyFire;
    private boolean seeFriendlyInvisibles;

    public Team build(Map map){
        Team team = new Team(map, this.internalName);
        team.setName(this.name);
        team.setColor(this.color);
        team.setFriendlyFireEnabled(this.friendlyFire);
        team.setSeeFriendlyInvisibles(this.seeFriendlyInvisibles);
        return team;
    }
}
