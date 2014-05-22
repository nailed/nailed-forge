package jk_5.nailed.client.particle;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.client.*;
import net.minecraft.client.particle.*;
import net.minecraft.world.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class ParticleHelper {

    private static Map<String, Class<? extends EntityFX>> particles = Maps.newHashMap();

    private ParticleHelper(){

    }

    static {
        particles.put("teleport", ParticleTeleport.class);
    }

    public static void spawnParticle(String name, double x, double y, double z, double motionX, double motionY, double motionZ){
        Minecraft mc = Minecraft.getMinecraft();
        if(mc != null && mc.renderViewEntity != null && mc.effectRenderer != null){
            int particleSetting = mc.gameSettings.particleSetting;

            if(particleSetting == 1 && mc.theWorld.rand.nextInt(3) == 0){
                particleSetting = 2;
            }
            if(particleSetting > 1){
                return;
            }

            try{
                EntityFX particle = particles.get(name).getConstructor(World.class, double.class, double.class, double.class, double.class, double.class, double.class).newInstance(mc.theWorld, x, y, z, motionX, motionY, motionZ);
                double dx = mc.renderViewEntity.posX - x;
                double dy = mc.renderViewEntity.posY - y;
                double dz = mc.renderViewEntity.posZ - z;
                double maxDistance = 256.0d;
                if(dx * dx + dy * dy + dz * dz > maxDistance){
                    return;
                }
                mc.effectRenderer.addEffect(particle);
            }catch(Exception e){
                //Something broke. Don't spawn it!
            }
        }
    }
}
