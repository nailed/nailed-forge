package jk_5.nailed.map.teleport;

import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportEventListenerEffect {

    @SubscribeEvent
    public void onTeleportStart(TeleportEvent.TeleportEventStart event) {
        if(event.options.isSpawnParticles()){
            NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.Particle(event.entity.posX, event.entity.posY, event.entity.posZ, "teleport"), event.entity.dimension);
        }
        playSound(event.entity, event.options);
    }

    @SubscribeEvent
    public void onTeleportEnd(TeleportEvent.TeleportEventEnd event) {
        if(event.options.isSpawnParticles()){
            NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.Particle(event.entity.posX, event.entity.posY, event.entity.posZ, "teleport"), event.entity.dimension);
        }
        playSound(event.entity, event.options);
        if(event.options.isClearInventory() && event.entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
            player.inventory.clearInventory(null, -1);
            player.inventoryContainer.detectAndSendChanges();
            if(!player.capabilities.isCreativeMode){
                player.updateHeldItem();
            }
        }
    }

    private static void playSound(Entity entity, TeleportOptions options) {
        if(entity instanceof EntityItem){
            entity.worldObj.playSoundAtEntity(entity, "nailed:teleport.pop", 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
        }else if(options.getSound() != null && !options.getSound().isEmpty()){
            entity.worldObj.playSoundAtEntity(entity, options.getSound(), 0.8F, entity.worldObj.rand.nextFloat() * 0.2F + 0.9F);
        }
    }
}
