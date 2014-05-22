package jk_5.nailed.map.teleport;

import net.minecraft.entity.*;

import cpw.mods.fml.common.eventhandler.*;

import net.minecraftforge.common.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.*;

public final class TeleportEventFactory {

    private TeleportEventFactory(){

    }

    public static boolean isLinkPermitted(Map orgin, Map destination, Entity entity, TeleportOptions options) {
        Event event = new TeleportEvent.TeleportEventAllow(orgin, destination, entity, options.reMake());
        return !MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onLinkStart(Map orgin, Map destination, Entity entity, TeleportOptions options) {
        Event event = new TeleportEvent.TeleportEventStart(orgin, destination, entity, options.reMake());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onExitWorld(Map orgin, Map destination, Entity entity, TeleportOptions options) {
        Event event = new TeleportEvent.TeleportEventExitWorld(orgin, destination, entity, options.reMake());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onEnterWorld(Map orgin, Map destination, Entity entity, TeleportOptions options) {
        Event event = new TeleportEvent.TeleportEventEnterWorld(orgin, destination, entity, options.reMake());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onLinkEnd(Map orgin, Map destination, Entity entity, TeleportOptions options) {
        Event event = new TeleportEvent.TeleportEventEnd(orgin, destination, entity, options.reMake());
        MinecraftForge.EVENT_BUS.post(event);
    }
}
