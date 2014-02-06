package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class WorldContext implements IDimensionContext {

    @Getter private final int dimensionId;

    public WorldContext(World world){
        this.dimensionId = world.provider.dimensionId;

    }
}
