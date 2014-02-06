package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.entity.Entity;

/**
 * No description given
 *
 * @author jk-5
 */
public class EntityContext implements ILocationContext, IRotationContext {

    @Getter private final double x, y, z;
    @Getter private final int dimensionId, entityId;
    @Getter private final float yaw, pitch;

    public EntityContext(Entity entity){
        this.dimensionId = entity.worldObj.provider.dimensionId;
        this.entityId = entity.getEntityId();
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
        this.pitch = entity.rotationPitch;
        this.yaw = entity.rotationYaw;
    }
}
