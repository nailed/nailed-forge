package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TeleportEventFactory {

    public static boolean isLinkPermitted(World world, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventAllow(world, entity, options.clone());
        if(MinecraftForge.EVENT_BUS.post(event)){
            return false;
        }
        return true;
    }

    public static void onLinkStart(World world, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventStart(world, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onExitWorld(Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventExitWorld(entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onEnterWorld(World world, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnterWorld(world, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onLinkEnd(World world, Entity entity, TeleportOptions options){
        Event event = new TeleportEvent.TeleportEventEnd(world, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
    }
}