package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TeleportEventFactory {

    public static boolean isLinkPermitted(World orgin, World destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventAllow(orgin, destination, entity, options.clone());
        if(MinecraftForge.EVENT_BUS.post(event)){
            return false;
        }
        return true;
    }

    public static void onLinkStart(World orgin, World destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventStart(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onExitWorld(World orgin, World destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventExitWorld(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onEnterWorld(World orgin, World destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnterWorld(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onLinkEnd(World orgin, World destination, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnd(orgin, destination, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }
}