package jk_5.nailed.map.teleport;

import jk_5.nailed.map.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class TeleportEvent extends Event {

    public final World origin;
    public final World destination;
    public final Entity entity;
    public final TeleportOptions options;

    public TeleportEvent(World origin, World destination, Entity entity, TeleportOptions info) {
        this.origin = origin;
        this.destination = destination;
        this.entity = entity;
        this.options = info;
    }

    public static class TeleportEventAlter extends TeleportEvent {
        public ChunkCoordinates spawn;
        public Float rotationYaw;

        public TeleportEventAlter(World origin, World destination, Entity entity, TeleportOptions info) {
            super(origin, destination, entity, info);
        }
    }

    public static class TeleportEventEnd extends TeleportEvent {
        public TeleportEventEnd(World destination, Entity entity, TeleportOptions info) {
            super(null, destination, entity, info);
        }
    }

    public static class TeleportEventEnterWorld extends TeleportEvent {
        public TeleportEventEnterWorld(World destination, Entity entity, TeleportOptions info) {
            super(null, destination, entity, info);
        }
    }

    public static class TeleportEventExitWorld extends TeleportEvent {
        public TeleportEventExitWorld(Entity entity, TeleportOptions info) {
            super(null, null, entity, info);
        }
    }

    public static class TeleportEventStart extends TeleportEvent {
        public TeleportEventStart(World origin, Entity entity, TeleportOptions info) {
            super(origin, null, entity, info);
        }
    }

    @Cancelable
    public static class TeleportEventAllow extends TeleportEvent {
        public Map destinationMap;
        public TeleportEventAllow(Map destination, Entity entity, TeleportOptions info) {
            super(null, destination.getWorld(), entity, info);
            this.destinationMap = destination;
        }
    }
}
