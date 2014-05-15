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

    public boolean isDone = false;

    @Nullable
    public BezierMovement(Location[] aLocation, int totalTicks){
        if(aLocation == null || aLocation.length < 2 || totalTicks < 1) return;
        this.aLocation = aLocation;
        this.totalTicks = totalTicks;
    }

    public Location getNextLocation() {
        Location location = getLocation(this.aLocation);
        if(this.currentTicks > this.totalTicks) this.isDone = true;
        ++this.currentTicks;
        return location;
    }

    private Location getLocation(Location[] aLocation){
        if(aLocation.length == 2) return getLinearLocation(aLocation[0], aLocation[1]);
        Location[] newLocation = new Location[aLocation.length - 1];
        for(int i = 0; i < aLocation.length - 1; ++i){
            newLocation[i] = getLinearLocation(aLocation[i], aLocation[i + 1]);
        }
        return getLocation(newLocation);
    }

    private Location getLinearLocation(Location s1, Location s2) {
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
}
