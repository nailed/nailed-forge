package jk_5.nailed.map.teleport;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.map.mappack.Spawnpoint;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeleportHelper {
    private static MinecraftServer mcServer = null;

    public static boolean travelEntity(World world, Entity entity, TeleportOptions options){
        if(world.isRemote) return false;
        if(options == null) return false;
        options = options.clone();
        int dimension = options.getDestinationID();
        Spawnpoint spawn = options.getCoordinates();
        if(!TeleportEventFactory.isLinkPermitted(entity.worldObj, world, entity, options)) return false;
        if(mcServer == null) mcServer = MinecraftServer.getServer();
        if((mcServer == null) || ((dimension != 0) && (!mcServer.getAllowNether()))) return false;
        WorldServer newworld = mcServer.worldServerForDimension(dimension);
        if(newworld == null){
            NailedLog.error("Cannot Link Entity to Dimension: Could not get World for Dimension " + dimension);
            return false;
        }
        if(spawn == null){
            spawn = new Spawnpoint(newworld.getSpawnPoint());
            options.setCoordinates(spawn);
        }
        TeleportEvent.TeleportEventAlter event = new TeleportEvent.TeleportEventAlter(world, newworld, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
        if(event.spawn != null) spawn = event.spawn;
        teleportEntity(newworld, entity, dimension, spawn, options);
        return true;
    }

    private static Entity teleportEntity(World newworld, Entity entity, int dimension, Spawnpoint spawn, TeleportOptions options){
        if(!TeleportEventFactory.isLinkPermitted(entity.worldObj, newworld, entity, options)){
            return null;
        }
        Entity mount = entity.ridingEntity;
        if(entity.ridingEntity != null){
            entity.mountEntity(null);
            mount = teleportEntity(newworld, mount, dimension, spawn, options);
        }
        boolean changingworlds = entity.worldObj != newworld;
        TeleportEventFactory.onLinkStart(entity.worldObj, newworld, entity, options);
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if((entity instanceof EntityPlayerMP)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.closeScreen();
            if(changingworlds){
                player.dimension = dimension;
                player.playerNetServerHandler.func_147359_a(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, newworld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                if(newworld.provider instanceof NailedWorldProvider){
                    ((NailedWorldProvider) newworld.provider).sendMapData(player);
                }
                ((WorldServer) entity.worldObj).getPlayerManager().removePlayer(player);
            }
        }
        World orgin = entity.worldObj;
        if(changingworlds){
            removeEntityFromWorld(entity.worldObj, entity);
        }
        TeleportEventFactory.onExitWorld(orgin, newworld, entity, options);

        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        ((WorldServer) newworld).theChunkProviderServer.loadChunk(spawn.posX >> 4, spawn.posZ >> 4);
        while(getCollidingWorldGeometry(newworld, entity.boundingBox, entity).size() != 0){
            spawn.posY += 1;
            entity.setPosition(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D);
        }
        if(changingworlds){
            if(!(entity instanceof EntityPlayer)){
                NBTTagCompound entityNBT = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(entityNBT);
                entity.isDead = true;
                entity = EntityList.createEntityFromNBT(entityNBT, newworld);
                if(entity == null) return null;
                entity.dimension = newworld.provider.dimensionId;
            }
            newworld.spawnEntityInWorld(entity);
            entity.setWorld(newworld);
        }
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        TeleportEventFactory.onEnterWorld(orgin, newworld, entity, options);
        newworld.updateEntityWithOptionalForce(entity, false);
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        if((entity instanceof EntityPlayerMP)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if(changingworlds) player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) newworld);
            player.playerNetServerHandler.func_147364_a(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
        }
        newworld.updateEntityWithOptionalForce(entity, false);
        if(((entity instanceof EntityPlayerMP)) && (changingworlds)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.theItemInWorldManager.setWorld((WorldServer) newworld);
            player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) newworld);
            player.mcServer.getConfigurationManager().syncPlayerInventory(player);
            Iterator iter = player.getActivePotionEffects().iterator();

            while(iter.hasNext()){
                PotionEffect effect = (PotionEffect) iter.next();
                player.playerNetServerHandler.func_147359_a(new S1DPacketEntityEffect(player.func_145782_y(), effect));
            }
            player.playerNetServerHandler.func_147359_a(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
        }
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        TeleportEventFactory.onLinkEnd(orgin, newworld, entity, options);
        if(mount != null){
            if((entity instanceof EntityPlayerMP)){
                newworld.updateEntityWithOptionalForce(entity, true);
            }
            entity.mountEntity(mount);
        }
        return entity;
    }

    private static void removeEntityFromWorld(World world, Entity entity){
        if((entity instanceof EntityPlayer)){
            EntityPlayer player = (EntityPlayer) entity;
            player.closeScreen();
            world.playerEntities.remove(player);
            world.updateAllPlayersSleepingFlag();
            int i = entity.chunkCoordX;
            int j = entity.chunkCoordZ;
            if((entity.addedToChunk) && (world.getChunkProvider().chunkExists(i, j))){
                world.getChunkFromChunkCoords(i, j).removeEntity(entity);
                world.getChunkFromChunkCoords(i, j).isModified = true;
            }
            world.loadedEntityList.remove(entity);
            world.onEntityRemoved(entity);
        }
        entity.isDead = false;
    }

    private static List getCollidingWorldGeometry(World world, AxisAlignedBB axisalignedbb, Entity entity){
        ArrayList collidingBoundingBoxes = new ArrayList();
        int i = MathHelper.floor_double(axisalignedbb.minX);
        int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
        int k = MathHelper.floor_double(axisalignedbb.minY);
        int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
        int i1 = MathHelper.floor_double(axisalignedbb.minZ);
        int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
        for(int k1 = i; k1 < j; k1++){
            for(int l1 = i1; l1 < j1; l1++){
                if(world.blockExists(k1, 64, l1)){
                    for(int i2 = k - 1; i2 < l; i2++){
                        Block block = world.func_147439_a(k1, i2, l1);
                        if(block != null){
                            block.func_149743_a(world, k1, i2, l1, axisalignedbb, collidingBoundingBoxes, entity);
                        }
                    }
                }
            }
        }
        return collidingBoundingBoxes;
    }
}