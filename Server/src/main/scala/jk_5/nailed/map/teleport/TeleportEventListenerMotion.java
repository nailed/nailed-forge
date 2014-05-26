package jk_5.nailed.map.teleport;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.teleport.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeleportEventListenerMotion {

    @SubscribeEvent
    public void onExitWorld(TeleportEvent.TeleportEventExitWorld event) {
        /*if(event.options.isMaintainMomentum()){
            float yaw = event.options.getLocation().getYaw();
            //noinspection SuspiciousNameCombination
            float rotationYaw = (float) (Math.atan2(event.entity.motionX, event.entity.motionZ) * 180.0D / Math.PI);
            double cos = Math.cos(Math.toRadians(-rotationYaw));
            double sin = Math.sin(Math.toRadians(-rotationYaw));
            double tempXmotion = cos * event.entity.motionX - sin * event.entity.motionZ;
            double tempZmotion = sin * event.entity.motionX + cos * event.entity.motionZ;
            event.entity.motionX = tempXmotion;
            event.entity.motionZ = tempZmotion;

            cos = Math.cos(Math.toRadians(yaw));
            sin = Math.sin(Math.toRadians(yaw));
            tempXmotion = cos * event.entity.motionX - sin * event.entity.motionZ;
            tempZmotion = sin * event.entity.motionX + cos * event.entity.motionZ;
            event.entity.motionX = tempXmotion;
            event.entity.motionZ = tempZmotion;
        }else{
            event.entity.motionX = event.entity.motionY = event.entity.motionZ = 0;
            event.entity.fallDistance = 0;
        }*/
        event.entity.motionY += 0.2;
    }
}
