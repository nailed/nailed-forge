package jk_5.nailed.api.zone;

import javax.annotation.*;

/**
 * Created by matthias on 9-5-14.
 */
public interface IZone {

    boolean isInZone(double x, double y, double z);
    IZone reMake();
    @Nonnull
    String getName();
}
