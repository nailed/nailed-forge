package jk_5.nailed.permissions.zone.types;

import com.google.common.base.*;

/**
 * Created by matthias on 9-5-14.
 */
public class SphereZone extends AbstractZone {

    private int x;
    private int y;
    private int z;
    private int r;

    public SphereZone(String name, int x, int y, int z, int r, boolean inverted) {
        super(name, inverted);
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    @Override
    public boolean isInZone(double x, double y, double z) {
        return (r >= Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) + (z - this.z) * (z - this.z))) ^ inverted;
    }

    @Override
    public SphereZone reMake() {
        return new SphereZone(this.name, this.x, this.y, this.z, this.r, this.inverted);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("inverted", inverted)
                .add("x", x)
                .add("y", y)
                .add("z", z)
                .add("r", r)
                .toString();
    }
}
