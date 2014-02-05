package jk_5.nailed.api.map.sign;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

/**
 * No description given
 *
 * @author jk-5
 */
public interface SignCommandHandler {

    public void onPlayerLeftMap(Map oldMap, Player player);
    public void onPlayerJoinMap(Map newMap, Player player);
    public void onChunkLoad(ChunkEvent.Load event);
    public void onChunkUnload(ChunkEvent.Unload event);
    public void onWatch(ChunkWatchEvent.Watch event);
    public void onUnwatch(ChunkWatchEvent.UnWatch event);
    public void onInteract(PlayerInteractEvent event);
    public void onSignAdded(String[] lines, int x, int y, int z);
    public Sign getSign(int x, int y, int z);
}
