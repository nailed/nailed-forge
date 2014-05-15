package jk_5.nailed.api.camera;

import jk_5.nailed.api.player.Player;

import java.util.List;

/**
 * Created by matthias on 15-5-14.
 */
public interface MovementHandler {
    public List<Player> getPlayers();
    public void updatePlayerLocations();
    public void removePlayerMovement(Player player);
    public void addPlayerMovement(Player player, IMovement movement);
}
