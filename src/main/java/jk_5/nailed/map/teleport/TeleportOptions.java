package jk_5.nailed.map.teleport;

import jk_5.nailed.map.Map;
import lombok.Data;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
public class TeleportOptions {

    private ChunkCoordinates coordinates;
    private float yaw;
    private Map destination;
    private int destinationID;
    private boolean maintainMomentum = false;
    private String sound = "";

    public TeleportOptions(Map destination, ChunkCoordinates coords, float yaw){
        this.destination = destination;
        this.destinationID = destination.getID();
        this.coordinates = new ChunkCoordinates(coords);
        this.yaw = yaw;
    }
}
