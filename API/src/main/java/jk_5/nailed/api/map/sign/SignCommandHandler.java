package jk_5.nailed.api.map.sign;

import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface SignCommandHandler {

    void onPlayerLeftMap(Map oldMap, Player player);
    void onPlayerJoinMap(Map newMap, Player player);
    void onChunkLoad(ChunkEvent.Load event);
    void onChunkUnload(ChunkEvent.Unload event);
    void onWatch(ChunkWatchEvent.Watch event);
    void onUnwatch(ChunkWatchEvent.UnWatch event);
    void onInteract(PlayerInteractEvent event);
    void onSignAdded(String[] lines, int x, int y, int z);
    Sign getSign(int x, int y, int z);
}
