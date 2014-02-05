package jk_5.nailed.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class RayTracer {

    public static MovingObjectPosition reTrace(World world, EntityPlayer player){
        return reTrace(world, player, getBlockReachDistance(player));
    }

    public static MovingObjectPosition reTrace(World world, EntityPlayer player, double reach){
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
