package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

/**
 * Created by matthias on 9-5-14.
 */
public class SquareZone implements IZone {
    private int x1;
    private int z1;
    private int x2;
    private int z2;

    public SquareZone(int x1, int z1, int x2, int z2){
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    public boolean isInZone(double x, double y, double z){
        return (x1 < x && x < x2 && z1 < z && z < z2);
    }

    public SquareZone clone(){
        return new SquareZone(this.x1, this.z1, this.x2, this.z2);
    }
}
