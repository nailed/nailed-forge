package jk_5.nailed.api.map.teleport;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.map.Location;

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

    public static Builder builder(){
        return new Builder();
    }

    private static class Builder {

        private Location location;
        private Map destination;
        private boolean maintainMomentum = false;
        private boolean spawnParticles = true;
        private boolean clearInventory = false;
        private String sound = "nailed:teleport";

        public Builder location(Location location){
            this.location = location;
            return this;
        }

        public Builder destination(Map destination){
            this.destination = destination;
            return this;
        }

        public Builder maintainMomentum(boolean maintainMomentum){
            this.maintainMomentum = maintainMomentum;
            return this;
        }

        public Builder spawnParticles(boolean spawnParticles){
            this.spawnParticles = spawnParticles;
            return this;
        }

        public Builder clearInventory(boolean clearInventory){
            this.clearInventory = clearInventory;
            return this;
        }

        public Builder sound(String sound){
            this.sound = sound;
            return this;
        }

        public TeleportOptions build(){
            return new TeleportOptions(location, destination, maintainMomentum, spawnParticles, clearInventory, sound);
        }
    }
}
