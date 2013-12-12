package jk_5.nailed.server;

import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.players.TeamUndefined;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class EventHandlerServer {

    @ForgeSubscribe
    public void onDie(LivingDeathEvent event){
        if(!(event.entity instanceof EntityPlayer)) return;
        Player player = PlayerRegistry.instance().getPlayer(((EntityPlayer) event.entity).username);
        if(player == null || player.getTeam() instanceof TeamUndefined) return;
        if(player.getTeam().shouldOverrideDefaultSpawnpoint()){
            ChunkCoordinates coords = player.getTeam().getSpawnPoint();
            player.getEntity().setSpawnChunk(coords, true);
        }
    }
}
