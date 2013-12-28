package jk_5.quakecraft;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;

/**
 * No description given
 *
 * @author jk-5
 */
public class ShootCooldownTickHandler implements ITickHandler {

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData){

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData){
        EntityPlayer player = (EntityPlayer) tickData[0];
        if(QuakecraftPlugin.instance.reloadCooldown.containsKey(player.username)){
            int ticks = QuakecraftPlugin.instance.reloadCooldown.get(player.username) + 1;
            QuakecraftPlugin.instance.reloadCooldown.put(player.username, ticks);
        }
    }

    @Override
    public EnumSet<TickType> ticks(){
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel(){
        return "QuakeCraft|ShootCooldown";
    }
}
