package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportEventListenerForge {

    @SubscribeEvent
    public void onTeleportEnd(TeleportEvent.TeleportEventEnd event){
        if((event.entity instanceof EntityPlayer)){
            //FMLCommonHandler.instance().firePlayerChangedDimensionEvent((EntityPlayer) event.entity, event.origin.provider.dimensionId, event.destination.provider.dimensionId);
        }
    }
}
