package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

/**
 * Created by matthias on 9-5-14.
 */
public class SquareZone implements IZone {
    private String name;
    private int x1;
    private int z1;
    private int x2;
    private int z2;
    private boolean inverted;

    public SquareZone(String name, int x1, int z1, int x2, int z2, boolean inverted){
        this.name = name;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.inverted = inverted;
    }

    public boolean isInZone(double x, double y, double z){
        return ((x1 < x && x < x2 && z1 < z && z < z2) ^ inverted);
    }

    public SquareZone reMake(){
        return new SquareZone(this.name, this.x1, this.z1, this.x2, this.z2, this.inverted);
    }

    public String getName(){
        return this.name;
    }
}
