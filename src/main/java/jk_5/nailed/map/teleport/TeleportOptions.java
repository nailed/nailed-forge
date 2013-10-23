package jk_5.nailed.map.teleport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
    private boolean maintainMomentum = false;
    private String sound = "";

    public TeleportOptions(ChunkCoordinates coords, float yaw){
        this.coordinates = new ChunkCoordinates(coords);
        this.yaw = yaw;
    }
}
