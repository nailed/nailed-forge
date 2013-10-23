package jk_5.nailed.map.teleport;

import jk_5.nailed.map.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportListener {

    @ForgeSubscribe
    public void isPermitted(TeleportEvent.TeleportEventAllow event){
        Entity entity = event.entity;
        World world = event.origin;
        Map destination = event.destinationMap;
        TeleportOptions options = event.options;

        if(entity.isDead || entity.worldObj != world || entity.riddenByEntity != null){
            event.setCanceled(true);
        }else if(entity.worldObj.provider.dimensionId == destination.getID()){
            event.setCanceled(true);
        }
    }

    @ForgeSubscribe
    public void onExitWorld(TeleportEvent.TeleportEventExitWorld event){
        this.handleMomentum(event.entity, event.options);
    }

    @ForgeSubscribe
    public void onEnd(TeleportEvent.TeleportEventEnd event){
        if(event.entity instanceof EntityMinecart){
            event.entity.motionX = 0;
            event.entity.motionZ = 0;
        }
    }

    private void handleMomentum(Entity entity, TeleportOptions options){
        if(options.isMaintainMomentum()){
            entity.motionX = entity.motionY = entity.motionZ = 0;
            entity.fallDistance = 0;
        }else{
            float yaw = options.getYaw();
            float rotationYaw = (float)(Math.atan2(entity.motionX, entity.motionZ) * 180.0D / Math.PI);

            double cos = Math.cos(Math.toRadians(-rotationYaw));
            double sin = Math.sin(Math.toRadians(-rotationYaw));
            double tempXmotion = cos * entity.motionX - sin * entity.motionZ;
            double tempZmotion = sin * entity.motionX + cos * entity.motionZ;
            entity.motionX = tempXmotion;
            entity.motionZ = tempZmotion;

            cos = Math.cos(Math.toRadians(yaw));
            sin = Math.sin(Math.toRadians(yaw));
            tempXmotion = cos * entity.motionX - sin * entity.motionZ;
            tempZmotion = sin * entity.motionX + cos * entity.motionZ;
            entity.motionX = tempXmotion;
            entity.motionZ = tempZmotion;
        }
        entity.motionY += 0.2D;
    }
}
