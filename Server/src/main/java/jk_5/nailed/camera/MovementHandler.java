package jk_5.nailed.camera;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matthias on 15-5-14.
 */
public class MovementHandler implements jk_5.nailed.api.camera.MovementHandler {
    private HashMap<Player, IMovement> movements = Maps.newHashMap();
    private List<Player> players = Lists.newArrayList();

    public void addPlayerMovement(Player player, IMovement movement){
        if(movement == null || player == null) return;
        if(players.contains(player)) {
            players.remove(player);
            movements.remove(player);
        }
        movements.put(player, movement);
        players.add(player);
    }

    public void removePlayerMovement(Player player){
        movements.remove(player);
        players.remove(player);
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    public void updatePlayerLocations(){
        this.updatePlayerLocations(null);
    }

    @SubscribeEvent
    public void updatePlayerLocations(TickEvent event){
        for(int i = players.size() - 1; i >=0; --i){
            Player player = players.get(i);
            player.setLocation(movements.get(player).getCurrentLocation());
            movements.get(player).tick();
            if (movements.get(player).isDone()) {
                movements.remove(player);
                players.remove(player);
                player.setGameMode(Gamemode.SURVIVAL);
            }
        }
    }
}
