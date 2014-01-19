package jk_5.quakecraft;

import jk_5.nailed.map.PvpIgnoringDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

/**
 * No description given
 *
 * @author jk-5
 */
public class DamageSourceRailgun extends EntityDamageSource implements PvpIgnoringDamageSource {

    public DamageSourceRailgun(Entity entity){
        super("railgun", entity);
        this.setDamageBypassesArmor();
    }

    @Override
    public boolean disableWhenPvpDisabled(){
        return false;
    }
}