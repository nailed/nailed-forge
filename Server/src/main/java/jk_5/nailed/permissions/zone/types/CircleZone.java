package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

/**
 * Created by matthias on 9-5-14.
 */
public class CircleZone implements IZone {
    private int x;
    private int z;
    private int r;

    public CircleZone(int x, int z, int r){
        this.x = x;
        this.z = z;
        this.r = r;
    }

    public boolean isInZone(double x, double y, double z){
        return (r >= Math.sqrt((x-this.x)*(x-this.x) + (z-this.z)*(z-this.z)));
    }

    public CircleZone clone(){
        return new CircleZone(this.x, this.z, this.r);
    }
}
