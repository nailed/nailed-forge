package jk_5.quakecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

/**
 * No description given
 *
 * @author jk-5
 */
public class DamageSourceRailgun extends EntityDamageSource {

    public DamageSourceRailgun(Entity entity){
        super("railgun", entity);
        this.setDamageBypassesArmor();
    }
}
