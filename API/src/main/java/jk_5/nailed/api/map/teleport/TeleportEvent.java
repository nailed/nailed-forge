package jk_5.nailed.api.map.teleport;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.map.Spawnpoint;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

@RequiredArgsConstructor
public class TeleportEvent extends Event {

    public final Map oldMap;
    public final Map newMap;
    public final Entity entity;
    public final TeleportOptions options;

    /**
     * Cancel this event to prevent teleporting.
     */
    @Cancelable
    public static class TeleportEventAllow extends TeleportEvent {
        public TeleportEventAllow(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }

    /**
     * Used to provide alternate values for the teleport
     */
    public static class TeleportEventAlter extends TeleportEvent {
        /** Set this to alter the teleport destination (null until set) */
        public Spawnpoint spawn;

        public TeleportEventAlter(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }

    /**
     * Called before the entity is teleported
     */
    public static class TeleportEventStart extends TeleportEvent {
        public TeleportEventStart(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }

    /**
     * Called when the entity leaves their current world but has not yet entered the next
     */
    public static class TeleportEventExitWorld extends TeleportEvent {
        public TeleportEventExitWorld(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }

    /**
     * Called when the entity enters the new world and their position has been approximated.
     * The entity is not fully associated or tuned with the world yet, and players have not been sent
     * server-side information such as weather and time.
     */
    public static class TeleportEventEnterWorld extends TeleportEvent {
        public TeleportEventEnterWorld(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }

    /**
     * Called once everything is finalized.
     * The entity is fully realized in the world and has been updated on clients.
     */
    public static class TeleportEventEnd extends TeleportEvent {
        public TeleportEventEnd(Map oldMap, Map newMap, Entity entity, TeleportOptions info) {
            super(oldMap, newMap, entity, info);
        }
    }
}
