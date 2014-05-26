package jk_5.nailed.camera;

import java.util.*;

import com.google.common.collect.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.camera.*;
import jk_5.nailed.api.player.*;

/**
 * Created by matthias on 15-5-14.
 */
public class MovementHandler implements jk_5.nailed.api.camera.MovementHandler {

    private HashMap<Player, IMovement> movements = Maps.newHashMap();
    private List<Player> players = Lists.newArrayList();

    public void addPlayerMovement(Player player, IMovement movement) {
        if(movement == null || player == null){
            return;
        }
        if(players.contains(player)){
            players.remove(player);
            movements.remove(player);
        }
        movements.put(player, movement);
        players.add(player);
    }

    public void removePlayerMovement(Player player) {
        movements.remove(player);
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void updatePlayerLocations() {
        this.updatePlayerLocations(null);
    }

    @SubscribeEvent
    public void updatePlayerLocations(TickEvent event) {
        for(int i = players.size() - 1; i >= 0; --i){
            Player player = players.get(i);
            player.setLocation(movements.get(player).getCurrentLocation());
            movements.get(player).tick();
            if(movements.get(player).isDone()){
                movements.remove(player);
                players.remove(player);
                player.setGameMode(Gamemode.SURVIVAL);
            }
        }
    }
}
