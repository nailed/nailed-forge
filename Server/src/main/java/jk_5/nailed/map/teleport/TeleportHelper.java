package jk_5.nailed.map.teleport;

import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Spawnpoint;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.map.gen.NailedWorldProvider;
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
import java.util.List;

public class TeleportHelper {
    private static MinecraftServer mcServer = null;

    public static boolean travelEntity(Entity entity, TeleportOptions options){
        Map destMap = options.getDestination();
        Map currentMap = NailedAPI.getMapLoader().getMap(entity.worldObj);
        World destWorld = destMap.getWorld();
        if(destWorld.isRemote) return false;
        if(options == null) return false;
        options = options.clone();
        int dimension = destMap.getID();
        Spawnpoint spawn = options.getCoordinates();
        if(!TeleportEventFactory.isLinkPermitted(currentMap, destMap, entity, options)) return false;
        if(mcServer == null) mcServer = MinecraftServer.getServer();
        if(mcServer == null) return false;
        WorldServer newworld = mcServer.worldServerForDimension(dimension);
        if(newworld == null){
            NailedLog.error("Cannot Link Entity to Dimension: Could not get World for Dimension " + dimension);
            return false;
        }
        if(spawn == null){
            spawn = new Spawnpoint(newworld.getSpawnPoint());
            options.setCoordinates(spawn);
        }
        TeleportEvent.TeleportEventAlter event = new TeleportEvent.TeleportEventAlter(currentMap, destMap, entity, options.clone());
        MinecraftForge.EVENT_BUS.post(event);
        if(event.spawn != null) spawn = event.spawn;
        teleportEntity(currentMap, destMap, entity, spawn, options);
        return true;
    }

    private static Entity teleportEntity(Map currentMap, Map destMap, Entity entity, Spawnpoint spawn, TeleportOptions options){
        int dimension = destMap.getID();
        World destWorld = destMap.getWorld();
        if(!TeleportEventFactory.isLinkPermitted(currentMap, destMap, entity, options)){
            return null;
        }
        Entity mount = entity.ridingEntity;
        if(entity.ridingEntity != null){
            entity.mountEntity(null);
            mount = teleportEntity(currentMap, destMap, mount, spawn, options);
        }
        boolean changingworlds = entity.worldObj != destWorld;
        TeleportEventFactory.onLinkStart(currentMap, destMap, entity, options);
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if((entity instanceof EntityPlayerMP)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.closeScreen();
            if(changingworlds){
                player.dimension = dimension;
                player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, destWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                if(destWorld.provider instanceof NailedWorldProvider){
                    ((NailedWorldProvider) destWorld.provider).sendMapData(player);
                }
                ((WorldServer) entity.worldObj).getPlayerManager().removePlayer(player);
            }
        }
        if(changingworlds){
            removeEntityFromWorld(entity.worldObj, entity);
        }
        TeleportEventFactory.onExitWorld(currentMap, destMap, entity, options);

        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        ((WorldServer) destWorld).theChunkProviderServer.loadChunk(spawn.posX >> 4, spawn.posZ >> 4);
        while(getCollidingWorldGeometry(destWorld, entity.boundingBox, entity).size() != 0){
            spawn.posY += 1;
            entity.setPosition(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D);
        }
        if(changingworlds){
            if(!(entity instanceof EntityPlayer)){
                NBTTagCompound entityNBT = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(entityNBT);
                entity.isDead = true;
                entity = EntityList.createEntityFromNBT(entityNBT, destWorld);
                if(entity == null) return null;
                entity.dimension = destWorld.provider.dimensionId;
            }
            destWorld.spawnEntityInWorld(entity);
            entity.setWorld(destWorld);
        }
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        TeleportEventFactory.onEnterWorld(currentMap, destMap, entity, options);
        destWorld.updateEntityWithOptionalForce(entity, false);
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        if((entity instanceof EntityPlayerMP)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if(changingworlds) player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) destWorld);
            player.playerNetServerHandler.setPlayerLocation(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
        }
        destWorld.updateEntityWithOptionalForce(entity, false);
        if(((entity instanceof EntityPlayerMP)) && (changingworlds)){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.theItemInWorldManager.setWorld((WorldServer) destWorld);
            player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) destWorld);
            player.mcServer.getConfigurationManager().syncPlayerInventory(player);

            for(Object obj : player.getActivePotionEffects()){
                player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), (PotionEffect) obj));
            }
            player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
        }
        entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, spawn.yaw, spawn.pitch);
        TeleportEventFactory.onLinkEnd(currentMap, destMap, entity, options);
        if(mount != null){
            if((entity instanceof EntityPlayerMP)){
                destWorld.updateEntityWithOptionalForce(entity, true);
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
                        Block block = world.getBlock(k1, i2, l1);
                        if(block != null){
                            block.addCollisionBoxesToList(world, k1, i2, l1, axisalignedbb, collidingBoundingBoxes, entity);
                        }
                    }
                }
            }
        }
        return collidingBoundingBoxes;
    }
}
