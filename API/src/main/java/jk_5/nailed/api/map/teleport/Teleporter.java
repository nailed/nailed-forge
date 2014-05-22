package jk_5.nailed.api.map.teleport;

import javax.annotation.*;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

/**
 * A simple and less buggy way to teleport Entities from coordinates to other coordinates or even to other worlds.
 *
 * @author jk-5
 */
public interface Teleporter {

    /**
     * Teleport the given entity to other coordinates or another world, specified by the options
     *
     * @param entity  The entity to teleport.
     * @param options The information about the location and coordinates where the entity should land.
     * @return true if the Entity was teleported successfully. false if it has been canceled or errored.
     */
    boolean teleportEntity(@Nonnull Entity entity, @Nonnull TeleportOptions options);

    /**
     * Teleport the given player entity in the specified dimension
     *
     * @param player    The player to respawn.
     * @param dimension The dimension the player has died in
     * @return the new player entity for this player
     */
    EntityPlayerMP respawnPlayer(EntityPlayerMP player, int dimension, boolean finishedEnd);
}
