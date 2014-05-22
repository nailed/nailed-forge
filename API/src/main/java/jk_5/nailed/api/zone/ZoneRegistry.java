package jk_5.nailed.api.zone;

import javax.annotation.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ZoneRegistry {

    void lockZones();
    void registerZones();
    @Nullable
    IZoneType getZoneType(@Nonnull String type);
}
