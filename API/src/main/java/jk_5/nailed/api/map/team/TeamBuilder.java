package jk_5.nailed.api.map.team;

import jk_5.nailed.api.ChatColor;
import jk_5.nailed.api.map.Map;

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

    public void setName(String name);
    public void setInternalName(String internalName);
    public void setColor(ChatColor color);
    public void setFriendlyFire(boolean isFriendlyFire);
    public void setSeeFriendlyInvisibles(boolean isSeeFriendlyInvisibles);

    public Team build(Map map);
}
