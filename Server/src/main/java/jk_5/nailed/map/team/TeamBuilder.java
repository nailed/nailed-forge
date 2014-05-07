package jk_5.nailed.map.team;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.players.NailedTeam;
import jk_5.nailed.util.ChatColor;

import javax.annotation.Nonnull;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamBuilder implements jk_5.nailed.api.map.team.TeamBuilder {

    private String name;
    private String internalName;
    private ChatColor color;
    private boolean friendlyFire;
    private boolean seeFriendlyInvisibles;

    @Override
    @Nonnull
    public Team build(Map map){
        Team team = new NailedTeam(map, this.internalName);
        team.setName(this.name);
        team.setColor(this.color);
        team.setFriendlyFireEnabled(this.friendlyFire);
        team.setSeeFriendlyInvisibles(this.seeFriendlyInvisibles);
        return team;
    }

    @Override
    public TeamBuilder setName(@Nonnull String name){
        this.name = name;
        return this;
    }

    @Override
    public TeamBuilder setInternalName(@Nonnull String internalName){
        this.internalName = internalName;
        return this;
    }

    @Override
    public TeamBuilder setColor(@Nonnull ChatColor color){
        this.color = color;
        return this;
    }

    @Override
    public TeamBuilder setFriendlyFire(boolean isFriendlyFire){
        this.friendlyFire = isFriendlyFire;
        return this;
    }

    @Override
    public TeamBuilder setSeeFriendlyInvisibles(boolean isSeeFriendlyInvisibles){
        this.seeFriendlyInvisibles = isSeeFriendlyInvisibles;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getInternalName() {
        return this.internalName;
    }

    @Override
    public ChatColor getColor() {
        return this.color;
    }

    @Override
    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    @Override
    public boolean isSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }
}
