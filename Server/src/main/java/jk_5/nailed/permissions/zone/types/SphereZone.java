package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

/**
 * Created by matthias on 9-5-14.
 */
public class SphereZone implements IZone {
    private int x;
    private int y;
    private int z;
    private int r;

    public SphereZone(int x, int y, int z, int r){
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public boolean isInZone(double x, double y, double z){
        return (r >= Math.sqrt((x-this.x) * (x-this.x) + (y-this.y) * (y-this.y) + (z-this.z) * (z-this.z)));
    }

    public SphereZone reMake(){
        return new SphereZone(this.x, this.y, this.z, this.r);
    }
}
