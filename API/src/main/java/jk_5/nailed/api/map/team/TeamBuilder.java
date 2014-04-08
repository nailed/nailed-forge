package jk_5.nailed.api.map.team;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.util.ChatColor;

import javax.annotation.Nonnull;

/**
 * No description given
 *
 * @author jk-5
 */
public interface TeamBuilder {

    public String getName();
    public String getInternalName();
    public ChatColor getColor();
    public boolean isFriendlyFire();
    public boolean isSeeFriendlyInvisibles();

    public TeamBuilder setName(@Nonnull String name);
    public TeamBuilder setInternalName(@Nonnull String internalName);
    public TeamBuilder setColor(@Nonnull ChatColor color);
    public TeamBuilder setFriendlyFire(boolean isFriendlyFire);
    public TeamBuilder setSeeFriendlyInvisibles(boolean isSeeFriendlyInvisibles);

    @Nonnull public Team build(Map map);
}
