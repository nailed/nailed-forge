package jk_5.nailed.api.map.teleport;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Spawnpoint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TeleportOptions {

    private Spawnpoint coordinates;
    private float yaw;
    private float pitch;
    private Map destination;
    private boolean maintainMomentum = false;
    private String sound = "";

    public TeleportOptions(Map destination, Spawnpoint coords, float yaw, float pitch){
        this.destination = destination;
        this.coordinates = new Spawnpoint(coords);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public TeleportOptions clone(){
        return new TeleportOptions(this.coordinates, this.yaw, this.pitch, this.destination, this.maintainMomentum, this.sound);
    }

    public int getDestinationID(){
        return this.destination.getID();
    }
}