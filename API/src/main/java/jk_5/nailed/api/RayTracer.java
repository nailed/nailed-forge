package jk_5.nailed.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class RayTracer {

    public static MovingObjectPosition rayTraceBlocks(World world, EntityPlayer player){
        return rayTraceBlocks(world, player, getBlockReachDistance(player));
    }

    public static MovingObjectPosition rayTraceBlocks(World world, EntityPlayer player, double reach){
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLook(1);
        Vec3 endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return world.rayTraceBlocks(headVec, endVec, true);
    }

    public static Vec3 getCorrectedHeadVec(EntityPlayer player){
        Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        if(player.worldObj.isRemote){
            v.yCoord += player.getEyeHeight() - player.getDefaultEyeHeight();
        }else{
            v.yCoord += player.getEyeHeight();
            if(player instanceof EntityPlayerMP && player.isSneaking()){
                v.yCoord -= 0.08;
            }
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static MovingObjectPosition rayTrace(EntityPlayer player){
        MovingObjectPosition result;

        World world = player.worldObj;
        MovingObjectPosition pointedBlock = RayTracer.rayTraceBlocks(world, player);
        result = pointedBlock;
        double rangeLimit = 500;
        double d1 = rangeLimit;
        Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + player.eyeHeight, player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit);
        if (pointedBlock != null){
            d1 = pointedBlock.hitVec.distanceTo(vec3);
        }
        Entity pointedEntity = null;
        float f1 = 1.0F;
        List<Entity> list = (List<Entity>) world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(lookVec.xCoord * rangeLimit, lookVec.yCoord * rangeLimit, lookVec.zCoord * rangeLimit).expand((double)f1, (double)f1, (double)f1));
        double d2 = d1;

        for (Entity entity : list){
            if (entity.canBeCollidedWith()){
                float f2 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)){
                    if (0.0D < d2 || d2 == 0.0D){
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
                }else if (movingobjectposition != null){
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D){
                        if (entity == entity.ridingEntity && !entity.canRiderInteract()){
                            if (d2 == 0.0D){
                                pointedEntity = entity;
                            }
                        }else{
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        if (pointedEntity != null && (d2 < d1 || result == null)){
            result = new MovingObjectPosition(pointedEntity);
        }
        return result;
    }

    public static double getBlockReachDistance(EntityPlayer player){
        return player.worldObj.isRemote ? getBlockReachDistance_client() :
                player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }

    private static double getBlockReachDistance_server(EntityPlayerMP player){
        return player.theItemInWorldManager.getBlockReachDistance();
    }

    @SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client(){
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
}
