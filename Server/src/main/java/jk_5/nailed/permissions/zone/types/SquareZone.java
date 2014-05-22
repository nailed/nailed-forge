package jk_5.nailed.permissions.zone.types;

import com.google.common.base.*;

/**
 * Created by matthias on 9-5-14.
 */
public class SquareZone extends AbstractZone {

    private int x1;
    private int z1;
    private int x2;
    private int z2;

    public SquareZone(String name, int x1, int z1, int x2, int z2, boolean inverted) {
        super(name, inverted);
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    @Override
    public boolean isInZone(double x, double y, double z) {
        return (x1 < x && x < x2 && z1 < z && z < z2) ^ inverted;
    }

    @Override
    public SquareZone reMake() {
        return new SquareZone(this.name, this.x1, this.z1, this.x2, this.z2, this.inverted);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("inverted", inverted)
                .add("x1", x1)
                .add("z1", z1)
                .add("x2", x2)
                .add("z2", z2)
                .toString();
    }
}
