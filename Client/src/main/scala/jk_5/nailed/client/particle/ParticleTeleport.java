package jk_5.nailed.client.particle;

import java.util.*;

import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ParticleTeleport extends EntityFX {

    private Random random;
    private float tempParticleScale;

    public ParticleTeleport(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
        this(world, x, y, z, motionX, motionY, motionZ, 3f);
    }

    public ParticleTeleport(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale){
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.1000000014901161D;
        this.motionY *= 0.1000000014901161D;
        this.motionZ *= 0.1000000014901161D;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.300000011920929D);
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.tempParticleScale = this.particleScale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) (this.particleMaxAge * scale);
        this.noClip = false;
        this.random = new Random();
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7){
        float var8 = (this.particleAge + par2) / this.particleMaxAge * 32.0F;

        if(var8 < 0.0F){
            var8 = 0.0F;
        }

        if(var8 > 1.0F){
            var8 = 1.0F;
        }

        this.particleScale = this.tempParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    public void onUpdate(){
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if(this.particleAge++ >= this.particleMaxAge){
            setDead();
        }

        setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionX += 0.005D * this.random.nextGaussian();
        this.motionY += 0.001D;
        this.motionZ += 0.005D * this.random.nextGaussian();
        moveEntity(this.motionX, this.motionY, this.motionZ);

        if(this.posY == this.prevPosY){
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if(this.onGround){
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
