package jk_5.nailed.api.camera;

import java.util.*;

import jk_5.nailed.api.player.*;

/**
 * Created by matthias on 15-5-14.
 */
public interface MovementHandler {

    List<Player> getPlayers();
    void updatePlayerLocations();
    void removePlayerMovement(Player player);
    void addPlayerMovement(Player player, IMovement movement);
}
