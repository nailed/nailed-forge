package jk_5.nailed.api.map.teleport;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.map.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TeleportOptions {

    private Location location;
    private Map destination;
    private boolean maintainMomentum = false;
    private boolean spawnParticles = true;
    private String sound = "nailed:teleport";

    public TeleportOptions(Map destination, Location location){
        this.destination = destination;
        this.location = new Location(location);
    }

    @Override
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public TeleportOptions clone(){
        return new TeleportOptions(this.location, this.destination, this.maintainMomentum, this.spawnParticles, this.sound);
    }

    public int getDestinationID(){
        return this.destination.getID();
    }
}
