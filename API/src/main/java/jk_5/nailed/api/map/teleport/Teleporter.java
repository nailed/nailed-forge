package jk_5.nailed.api.map.teleport;

import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * A simple and less buggy way to teleport Entities from coordinates to other coordinates or even to other worlds.
 *
 * @author jk-5
 */
public interface Teleporter {

    /**
     * Teleport the given entity to other coordinates or another world, specified by the options
     *
     * @param entity The entity to teleport.
     * @param options The information about the location and coordinates where the entity should land.
     * @return true if the Entity was teleported successfully. false if it has been canceled or errored.
     */
    public boolean teleportEntity(@Nonnull Entity entity, @Nonnull TeleportOptions options);
}
