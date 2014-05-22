package jk_5.nailed.api.map.teleport;

import jk_5.nailed.api.map.*;
import jk_5.nailed.map.*;

import lombok.*;
import lombok.experimental.*;

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
    private boolean clearInventory = false;
    private String sound = "nailed:teleport";

    public TeleportOptions(Map destination, Location location) {
        this.destination = destination;
        this.location = new Location(location);
    }

    public TeleportOptions reMake() {
        return new TeleportOptions(this.location, this.destination, this.maintainMomentum, this.spawnParticles, this.clearInventory, this.sound);
    }

    public int getDestinationID() {
        return this.destination.getID();
    }
}
