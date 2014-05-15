package jk_5.nailed.camera.Movements;

import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.map.Location;

import javax.annotation.Nullable;

/**
 * Created by matthias on 15-5-14.
 */
public class LinearMovement implements IMovement {
    private Location startLocation;
    private Location endLocation;
    private int totalTicks;
    private int currentTicks = 0;

    public boolean isDone = false;

    @Nullable
    public LinearMovement(Location startLocation, Location endLocation, int totalTicks){
        if(startLocation == null || endLocation == null || totalTicks < 1) return;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.totalTicks = totalTicks;
    }

    public Location getNextLocation(){
        Location s1 = this.startLocation;
        Location s2 = this.endLocation;
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
        ++this.currentTicks;
        if (this.currentTicks > this.totalTicks) this.isDone = true;
        return new Location(s1.getX() + dx * f, s1.getY() + dy * f, s1.getZ() + dz * f, s1.getPitch() + dPitch * f, s1.getYaw() + dYaw * f);
    }

    public boolean isDone(){
        return this.isDone;
    }
}
