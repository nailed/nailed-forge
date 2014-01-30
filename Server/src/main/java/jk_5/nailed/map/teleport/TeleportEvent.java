package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.mappack.Spawnpoint;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

@RequiredArgsConstructor
public abstract class TeleportEvent extends Event {

    /** The world the entity is leaving.  May be null. */
    public final Map origin;
    /** The destination world.  May be null. */
    public final Map destination;
    /** The entity being linked */
    public final Entity entity;
    /** The link descriptor.  You should not modify this object in any way */
    public final TeleportOptions options;

    /**
     * Cancel this event to prevent linking.
     */
    @Cancelable
    public static class TeleportEventAllow extends TeleportEvent {
        public TeleportEventAllow(Map origin, Map destination, Entity entity, TeleportOptions info) {
            super(origin, destination, entity, info);
        }
    }

    /**
     * Used to provide alternate values for the link
     */
    public static class TeleportEventAlter extends TeleportEvent {
        /** Set this to alter the link (null until set) */
        public Spawnpoint spawn;

        public TeleportEventAlter(Map origin, Map destination, Entity entity, TeleportOptions info) {
            super(origin, destination, entity, info);
        }
    }

    /**
     * Called before the entity is linked
     */
    public static class TeleportEventStart extends TeleportEvent {
        public TeleportEventStart(Map origin, Map destination, Entity entity, TeleportOptions info) {
            super(origin, destination, entity, info);
        }
    }

    /**
     * Called when the entity leaves their current world but has not yet entered the next
     */
    public static class TeleportEventExitWorld extends TeleportEvent {
        public TeleportEventExitWorld(Map orgin, Map destination, Entity entity, TeleportOptions info) {
            super(orgin, destination, entity, info);
        }
    }

    /**
     * Called when the entity enters the new world and their position has been approximated.
     * The entity is not fully associated or tuned with the world yet, and players have not been sent
     * server-side information such as weather and time.
     */
    public static class TeleportEventEnterWorld extends TeleportEvent {
        public TeleportEventEnterWorld(Map orgin, Map destination, Entity entity, TeleportOptions info) {
            super(orgin, destination, entity, info);
        }
    }

    /**
     * Called once everything is finalized.
     * The entity is fully realized in the world and has been updated on clients.
     */
    public static class TeleportEventEnd extends TeleportEvent {
        public TeleportEventEnd(Map orgin, Map destination, Entity entity, TeleportOptions info) {
            super(orgin, destination, entity, info);
        }
    }
}
