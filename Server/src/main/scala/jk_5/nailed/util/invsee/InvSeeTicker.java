package jk_5.nailed.util.invsee;

import com.google.common.collect.*;

import net.minecraft.entity.player.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvSeeTicker {

    public static HashMultimap<EntityPlayer, InventoryOtherPlayer> map = HashMultimap.create();

    public static void register(InventoryOtherPlayer inventory) {
        map.put(inventory.getOwner(), inventory);
    }

    public static void unregister(InventoryOtherPlayer inventory) {
        map.remove(inventory.getOwner(), inventory);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            return;
        }
        if(map.containsKey(event.player)){
            for(InventoryOtherPlayer inventory : map.get(event.player)){
                inventory.update();
            }
        }
    }
}
