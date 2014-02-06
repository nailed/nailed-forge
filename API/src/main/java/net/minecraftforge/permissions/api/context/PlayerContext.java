package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class PlayerContext extends EntityLivingContext implements INameContext {

    @Getter private final String name;

    public PlayerContext(EntityPlayer entity){
        super(entity);
        this.name = entity.getGameProfile().getName();
    }
}
