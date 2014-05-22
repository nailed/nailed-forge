package jk_5.nailed.camera.movements;

import javax.annotation.*;

import jk_5.nailed.api.camera.*;
import jk_5.nailed.map.*;

/**
 * Created by matthias on 15-5-14.
 */
public class LinearMovement implements IMovement {

    public boolean isDone = false;

    private Location startLocation;
    private Location endLocation;
    private int totalTicks;
    private int currentTicks = 0;
    private boolean followVision;

    @Nullable
    public LinearMovement(Location startLocation, Location endLocation, int totalTicks, boolean followVision) {
        if(startLocation == null || endLocation == null || totalTicks < 1){
            return;
        }
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.totalTicks = totalTicks;
        this.followVision = followVision;
    }

    public Location getLastLocation() {
        return getLocation(this.startLocation, this.endLocation, this.totalTicks, this.currentTicks - 1);
    }

    public Location getCurrentLocation() {
        Location currentLocation = getLocation(this.startLocation, this.endLocation, this.totalTicks, this.currentTicks);
        if(!this.followVision){
            return currentLocation;
        }

        Location lastLocation = getLastLocation();
        Location nextLocation = getNextLocation();
        double dx = nextLocation.getX() - lastLocation.getX();
        double dy = nextLocation.getY() - lastLocation.getY();
        double dz = nextLocation.getZ() - lastLocation.getZ();
        currentLocation.setPitch((float) Math.asin(dx / dy));
        currentLocation.setPitch((float) Math.asin(dz / Math.sqrt(dy * dy + dx * dx)));

        return currentLocation;
    }

    public Location getNextLocation() {
        return getLocation(this.startLocation, this.endLocation, this.totalTicks, this.currentTicks + 1);
    }

    private Location getLocation(Location s1, Location s2, int totalTicks, int currentTicks) {
        double dx = s1.getX() - s2.getX();
        double dy = s1.getY() - s2.getY();
        double dz = s1.getZ() - s2.getZ();
        float f = totalTicks / currentTicks;
        float dPitch = s1.getPitch() - s2.getPitch();
        float dYaw = s1.getYaw() - s2.getYaw();
        if(dPitch > 180){
            dPitch -= 360;
        }
        if(dPitch < -180){
            dPitch += 360;
        }
        if(dYaw > 180){
            dYaw -= 360;
        }
        if(dYaw < 180){
            dYaw += 360;
        }
        return new Location(s1.getX() + dx * f, s1.getY() + dy * f, s1.getZ() + dz * f, s1.getPitch() + dPitch * f, s1.getYaw() + dYaw * f);
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void tick() {
        ++this.currentTicks;
    }
}
