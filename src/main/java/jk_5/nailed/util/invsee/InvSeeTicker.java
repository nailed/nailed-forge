package jk_5.nailed.util.invsee;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvSeeTicker implements ITickHandler {

    public static HashMultimap<EntityPlayer, InventoryOtherPlayer> map = HashMultimap.create();

    public static void register(InventoryOtherPlayer inventory){
        map.put(inventory.getOwner(), inventory);
    }

    public static void unregister(InventoryOtherPlayer inventory){
        map.remove(inventory.getOwner(), inventory);
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData){
        if(map.containsKey(tickData[0])){
            for(InventoryOtherPlayer inventory : map.get((EntityPlayer) tickData[0])){
                inventory.update();
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData){

    }

    @Override
    public EnumSet<TickType> ticks(){
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel(){
        return "Nailed:InvSee Ticker";
    }
}
