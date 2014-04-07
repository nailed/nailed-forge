package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.teleport.TeleportEvent;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

public class TeleportEventFactory {

    public static boolean isLinkPermitted(Map orgin, Map destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventAllow(orgin, destination, entity, options.clone());
        if(MinecraftForge.EVENT_BUS.post(event)){
            return false;
        }
        return true;
    }

    public static void onLinkStart(Map orgin, Map destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventStart(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onExitWorld(Map orgin, Map destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventExitWorld(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onEnterWorld(Map orgin, Map destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnterWorld(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onLinkEnd(Map orgin, Map destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnd(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }
}