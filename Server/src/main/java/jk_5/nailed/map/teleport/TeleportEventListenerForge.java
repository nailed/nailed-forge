package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.map.teleport.TeleportEvent;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportEventListenerForge {

    @SubscribeEvent
    public void onTeleportEnd(TeleportEvent.TeleportEventEnd event){
        if(event.entity instanceof EntityPlayer && event.oldMap != event.newMap){
            FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayer) event.entity, event.oldMap.getID(), event.newMap.getID());
        }
    }
}
