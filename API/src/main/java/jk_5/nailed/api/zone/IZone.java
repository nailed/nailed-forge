package jk_5.nailed.api.zone;

import javax.annotation.Nonnull;

/**
 * Created by matthias on 9-5-14.
 */
public interface IZone {
    public boolean isInZone(double x, double y, double z);
    public IZone reMake();
    @Nonnull
    public String getName();
}
