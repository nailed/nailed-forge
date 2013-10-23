package jk_5.nailed.map.teleport;

import jk_5.nailed.map.Map;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportEventFactory {

    public static boolean isTeleportationPermitted(Map destination, Entity entity, TeleportOptions options) {
        return !MinecraftForge.EVENT_BUS.post(new TeleportEvent.TeleportEventAllow(destination, entity, options));
    }

    public static void onStartTeleport(World worldObj, Entity entity, TeleportOptions options) {
        MinecraftForge.EVENT_BUS.post(new TeleportEvent.TeleportEventStart(worldObj, entity, options));
    }

    public static void onExitWorld(Entity entity, TeleportOptions options) {
        MinecraftForge.EVENT_BUS.post(new TeleportEvent.TeleportEventExitWorld(entity, options));
    }

    public static void onEnterWorld(WorldServer newWorld, Entity entity, TeleportOptions options) {
        MinecraftForge.EVENT_BUS.post(new TeleportEvent.TeleportEventEnterWorld(newWorld, entity, options));
    }

    public static void onEndTeleport(WorldServer newWorld, Entity entity, TeleportOptions options) {
        MinecraftForge.EVENT_BUS.post(new TeleportEvent.TeleportEventEnd(newWorld, entity, options));
    }
}
