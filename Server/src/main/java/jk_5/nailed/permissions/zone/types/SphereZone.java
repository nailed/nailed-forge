package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

import javax.annotation.Nonnull;

/**
 * Created by matthias on 9-5-14.
 */
public class SphereZone implements IZone {

    private String name;
    private int x;
    private int y;
    private int z;
    private int r;
    private boolean inverted;

    public SphereZone(String name, int x, int y, int z, int r, boolean inverted){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.inverted = inverted;
    }

    @Override
    public boolean isInZone(double x, double y, double z){
        return ((r >= Math.sqrt((x-this.x) * (x-this.x) + (y-this.y) * (y-this.y) + (z-this.z) * (z-this.z))) ^ inverted);
    }

    @Override
    public SphereZone reMake(){
        return new SphereZone(this.name, this.x, this.y, this.z, this.r, this.inverted);
    }

    @Nonnull
    @Override
    public String getName(){
        return this.name;
    }
}
