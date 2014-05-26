package jk_5.nailed.permissions.zone.types;

import com.google.common.base.*;
import jk_5.nailed.map.Point;

/**
 * Created by matthias on 9-5-14.
 */
public class CircleZone extends AbstractZone {

    private int x;
    private int z;
    private int r;

    public CircleZone(String name, int x, int z, int r, boolean inverted) {
        super(name, inverted);
        this.x = x;
        this.z = z;
        this.r = r;
    }

    @Override
    public boolean isInZone(double x, double y, double z) {
        return (r >= Math.sqrt((x - this.x) * (x - this.x) + (z - this.z) * (z - this.z))) ^ inverted;
    }

    @Override
    public CircleZone reMake() {
        return new CircleZone(this.name, this.x, this.z, this.r, this.inverted);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("inverted", inverted)
                .add("x", x)
                .add("z", z)
                .add("r", r)
                .toString();
    }
}
