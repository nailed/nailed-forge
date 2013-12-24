package jk_5.quakecraft;

import codechicken.lib.raytracer.RayTracer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "QuakeCraft", name = "QuakeCraft", dependencies = "required-after:Nailed")
@NetworkMod
public class QuakecraftPlugin {

    @Mod.Instance("QuakeCraft")
    public static QuakecraftPlugin instance;

    @SidedProxy(modId = "QuakeCraft", clientSide = "jk_5.quakecraft.ProxyClient", serverSide = "jk_5.quakecraft.ProxyCommon")
    public static ProxyCommon proxy;

    public static Random rand = new Random();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if(event.getSide().isServer()){
            MinecraftForge.EVENT_BUS.register(this);
        }
        EntityRegistry.registerModEntity(EntityBullet.class, "entityBullet", 3, this, 128, 5, true);
        proxy.register(this);
    }

    @ForgeSubscribe
    public void onInteract(PlayerInteractEvent event){
        Map map = MapLoader.instance().getMap(event.entity.worldObj);
        if(this.isQuakecraft(map)){
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR){
                ItemStack held = event.entityPlayer.getHeldItem();
                if(held != null && held.getItem() instanceof ItemHoe){
                    MovingObjectPosition pos = RayTracer.reTrace(event.entity.worldObj, event.entityPlayer, 500);

                    if(pos != null && pos.typeOfHit != null){
                        if(pos.typeOfHit == EnumMovingObjectType.ENTITY){
                            System.out.println(pos.entityHit);
                        }else if(pos.typeOfHit == EnumMovingObjectType.TILE){
                            System.out.println(pos.blockX + " " + pos.blockY + " " + pos.blockZ);
                        }
                    }else{
                        System.out.println("No hit");
                    }
                    EntityBullet ns = new EntityBullet(event.entity.worldObj, event.entityPlayer);
                    event.entity.worldObj.spawnEntityInWorld(ns);
                    event.entity.worldObj.playSoundAtEntity(event.entity, "mob.blaze.hit", 2.0f, 4.0f);
                }
            }
        }
    }

    public MovingObjectPosition getFacedObject(EntityPlayer from){
        //float par3 = 0.8F;
        double x = from.posX;
        double y = from.posY;
        double z = from.posZ;
        x -= MathHelper.cos(from.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        y -= 0.2D;
        z -= MathHelper.sin(from.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        double dX = -MathHelper.sin(from.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(from.rotationPitch / 180.0F * (float) Math.PI);
        double dY = -MathHelper.sin(from.rotationPitch / 180.0F * (float) Math.PI);
        double dZ = MathHelper.cos(from.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(from.rotationPitch / 180.0F * (float) Math.PI);

        float var9 = MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
        dX /= var9;
        dY /= var9;
        dZ /= var9;
        dX += rand.nextGaussian() * 0.007499999832361937D;
        dY += rand.nextGaussian() * 0.007499999832361937D;
        dZ += rand.nextGaussian() * 0.007499999832361937D;
        dX *= 1.2;
        dY *= 1.2;
        dZ *= 1.2;
        //float var10 = MathHelper.sqrt_double(x * x + z * z);
        //prevRotationYaw = rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
        //prevRotationPitch = rotationPitch = (float) (Math.atan2(y, var10) * 180.0D / Math.PI);

        Vec3 vec1 = from.worldObj.getWorldVec3Pool().getVecFromPool(x, y, z);
        Vec3 vec2 = from.worldObj.getWorldVec3Pool().getVecFromPool(x + dX, y + dY, z + dZ);

        return from.worldObj.rayTraceBlocks_do_do(vec1, vec2, true, true);
    }

    public boolean isQuakecraft(Map map){
        return map.getMappack().getMappackMetadata().getGameType().equals("quakecraft");
    }
}
