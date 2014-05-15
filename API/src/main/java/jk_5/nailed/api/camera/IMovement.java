package jk_5.nailed.api.camera;

import jk_5.nailed.map.Location;

import javax.annotation.Nullable;

/**
 * Created by matthias on 15-5-14.
 */

@Nullable
public interface IMovement {
    public Location getLastLocation();
    public Location getCurrentLocation();
    public Location getNextLocation();
    public boolean isDone();
    public void tick();
}
