package jk_5.nailed.permissions.zone.types;

import jk_5.nailed.api.zone.IZone;

import javax.annotation.Nonnull;

/**
 * Created by matthias on 9-5-14.
 */
public class CircleZone implements IZone {

    private String name;
    private int x;
    private int z;
    private int r;
    private boolean inverted;

    public CircleZone(String name, int x, int z, int r, boolean inverted){
        this.name = name;
        this.x = x;
        this.z = z;
        this.r = r;
        this.inverted = inverted;
    }

    @Override
    public boolean isInZone(double x, double y, double z){
        return ((r >= Math.sqrt((x-this.x)*(x-this.x) + (z-this.z)*(z-this.z))) ^ inverted);
    }

    @Override
    public CircleZone reMake(){
        return new CircleZone(this.name, this.x, this.z, this.r, this.inverted);
    }

    @Nonnull
    @Override
    public String getName(){
        return this.name;
    }
}
