package net.minecraftforge.permissions.api.context;

import net.minecraft.entity.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class EntityLivingContext extends EntityContext implements IHealthContext {

    @Getter
    private final float currentHealth, maxHealth;

    public EntityLivingContext(EntityLivingBase entity) {
        super(entity);
        this.maxHealth = entity.getMaxHealth();
        this.currentHealth = entity.getHealth();
    }
}
