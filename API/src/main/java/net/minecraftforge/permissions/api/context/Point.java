package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.dispenser.ILocation;

/**
 * No description given
 *
 * @author jk-5
 */
public class Point implements ILocationContext {

    @Getter private final double x, y, z;
    @Getter private final int dimensionId;

    public Point(double x, double y, double z, int dimensionId){
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensionId = dimensionId;
    }

    public Point(ILocation location){
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.dimensionId = location.getWorld().provider.dimensionId;
    }
}
