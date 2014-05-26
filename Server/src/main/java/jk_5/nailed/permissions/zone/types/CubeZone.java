package jk_5.nailed.permissions.zone.types;

import com.google.common.base.*;
import jk_5.nailed.map.Point;

/**
 * Created by matthias on 9-5-14.
 * <p/>
 * Secure non-break zone
 */
public class CubeZone extends AbstractZone {

    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;

    public CubeZone(String name, int x1, int y1, int z1, int x2, int y2, int z2, boolean inverted) {
        super(name, inverted);
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    @Override
    public boolean isInZone(double x, double y, double z) {
        return (x1 < x && x < x2 && y1 < y && y < y2 && z1 < z && z < z2) ^ inverted;
    }

    @Override
    public CubeZone reMake() {
        return new CubeZone(this.name, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2, this.inverted);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("inverted", inverted)
                .add("x1", x1)
                .add("y1", y1)
                .add("z1", z1)
                .add("x2", x2)
                .add("y2", y2)
                .add("z2", z2)
                .toString();
    }
}
