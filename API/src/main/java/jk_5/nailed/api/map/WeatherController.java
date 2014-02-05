package jk_5.nailed.api.map;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * No description given
 *
 * @author jk-5
 */
public interface WeatherController {

    public double getRainingStrength();
    public double getThunderingStrength();
    public void updateRaining();
    public void tick(World worldObj, Chunk chunk);
    public void clear();
    public void toggleRain();
}
