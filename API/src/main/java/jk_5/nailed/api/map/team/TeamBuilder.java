package jk_5.nailed.api.map.team;

import javax.annotation.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface TeamBuilder {

    String getName();
    String getInternalName();
    ChatColor getColor();
    boolean isFriendlyFire();
    boolean isSeeFriendlyInvisibles();

    TeamBuilder setName(@Nonnull String name);
    TeamBuilder setInternalName(@Nonnull String internalName);
    TeamBuilder setColor(@Nonnull ChatColor color);
    TeamBuilder setFriendlyFire(boolean isFriendlyFire);
    TeamBuilder setSeeFriendlyInvisibles(boolean isSeeFriendlyInvisibles);

    @Nonnull
    Team build(Map map);
}
