package jk_5.nailed.api.map;

import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface WeatherController {

    double getRainingStrength();
    double getThunderingStrength();
    void updateRaining();
    void tick(World worldObj, Chunk chunk);
    void clear();
    void toggleRain();
}
