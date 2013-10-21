package jk_5.nailed.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
@AllArgsConstructor
public class TeleportOptions {

    private ChunkCoordinates coordinates;
    private float yaw;
}
