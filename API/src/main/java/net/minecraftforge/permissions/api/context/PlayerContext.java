package net.minecraftforge.permissions.api.context;

import net.minecraft.entity.player.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PlayerContext extends EntityLivingContext implements INameContext {

    @Getter
    private final String name;

    public PlayerContext(EntityPlayer entity) {
        super(entity);
        this.name = entity.getGameProfile().getName();
    }
}
