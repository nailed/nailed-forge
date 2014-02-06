package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;

/**
 * No description given
 *
 * @author jk-5
 */
public class EntityLivingContext extends EntityContext implements IHealthContext {

    @Getter private final float currentHealth, maxHealth;

    public EntityLivingContext(EntityLivingBase entity){
        super(entity);
        this.maxHealth = entity.getMaxHealth();
        this.currentHealth = entity.getHealth();
    }
}
