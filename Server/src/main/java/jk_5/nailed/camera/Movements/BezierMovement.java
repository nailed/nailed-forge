package jk_5.nailed.camera.Movements;

import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.map.Location;

import javax.annotation.Nullable;

/**
 * Created by matthias on 15-5-14.
 */
public class BezierMovement implements IMovement {
    private Location[] aLocation;
    private int totalTicks;
    private int currentTicks = 0;
    private boolean followVision;

    public boolean isDone = false;

    @Nullable
    public BezierMovement(Location[] aLocation, int totalTicks, boolean followVision){
        if(aLocation == null || aLocation.length < 2 || totalTicks < 1) return;
        this.aLocation = aLocation;
        this.totalTicks = totalTicks;
        this.followVision = followVision;
    }

    public Location getLastLocation() {
        return getLocation(this.aLocation, this.totalTicks, this.currentTicks -1);
    }

    public Location getCurrentLocation() {
        Location currentLocation = getLocation(this.aLocation, this.totalTicks, this.currentTicks);
        if (!this.followVision) return currentLocation;

        Location lastLocation = getLastLocation();
        Location nextLocation = getNextLocation();
        double dx = nextLocation.getX() - lastLocation.getX();
        double dy = nextLocation.getY() - lastLocation.getY();
        double dz = nextLocation.getZ() - lastLocation.getZ();
        currentLocation.setPitch((float) (Math.atan(dx/dy) / Math.PI * 180));
        currentLocation.setPitch((float) (Math.asin(dz/Math.sqrt(dy*dy + dx * dx)) / Math.PI * 180));

        return currentLocation;
    }

    public Location getNextLocation() {
        return getLocation(this.aLocation, this.totalTicks, this.currentTicks + 1);
    }

    private Location getLocation(Location[] aLocation, int totalTicks, int currentTicks){
        if(aLocation.length == 2) return getLinearLocation(aLocation[0], aLocation[1], totalTicks, currentTicks);
        Location[] newLocation = new Location[aLocation.length - 1];
        for(int i = 0; i < aLocation.length - 1; ++i){
            newLocation[i] = getLinearLocation(aLocation[i], aLocation[i + 1], totalTicks, currentTicks);
        }
        return getLocation(newLocation, totalTicks, currentTicks);
    }

    private Location getLinearLocation(Location s1, Location s2, int totalTicks, int currentTicks) {
        double dx = s1.getX() - s2.getX();
        double dy = s1.getY() - s2.getY();
        double dz = s1.getZ() - s2.getZ();
        float f = totalTicks / currentTicks;
        float dPitch = s1.getPitch() - s2.getPitch();
        float dYaw = s1.getYaw() - s2.getYaw();
        if(dPitch > 180) dPitch -= 360;
        if(dPitch < -180) dPitch += 360;
        if(dYaw > 180) dYaw -= 360;
        if(dYaw < 180) dYaw += 360;
        return new Location(s1.getX() + dx * f, s1.getY() + dy * f, s1.getZ() + dz * f, s1.getPitch() + dPitch * f, s1.getYaw() + dYaw * f);
    }

    public boolean isDone(){
        return this.isDone;
    }

    public void tick(){
        ++this.currentTicks;
    }
}
