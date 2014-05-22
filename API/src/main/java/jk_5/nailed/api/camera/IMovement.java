package jk_5.nailed.api.camera;

import javax.annotation.*;

import jk_5.nailed.map.*;

/**
 * Created by matthias on 15-5-14.
 */

@Nullable
public interface IMovement {

    Location getLastLocation();
    Location getCurrentLocation();
    Location getNextLocation();
    boolean isDone();
    void tick();
}
