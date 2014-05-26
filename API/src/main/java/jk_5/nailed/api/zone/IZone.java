package jk_5.nailed.api.zone;

import jk_5.nailed.map.Point;

import javax.annotation.*;

/**
 * Created by matthias on 9-5-14.
 */
public interface IZone {

    boolean isInZone(double x, double y, double z);
    boolean isInZone(Point point);
    IZone reMake();
    @Nonnull
    String getName();
}
