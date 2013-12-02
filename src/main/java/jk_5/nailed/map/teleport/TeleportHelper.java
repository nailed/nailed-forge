package jk_5.nailed.map.teleport;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.NailedLog;
import jk_5.nailed.map.gen.NailedWorldProvider;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@SideOnly(Side.SERVER)
public class TeleportHelper {

    private static MinecraftServer mcServer = null;

    public static void travelEntity(World from, Entity entity, TeleportOptions options){
        if(options == null) return;
        int toDimension = options.getDestinationID();
        ChunkCoordinates coords = options.getCoordinates();
        float yaw = options.getYaw();
        if(from.isRemote || !TeleportEventFactory.isTeleportationPermitted(from, entity, options)) return;
        if(mcServer == null) mcServer = MinecraftServer.getServer();
        if(mcServer == null) return;
        WorldServer newWorld = mcServer.worldServerForDimension(toDimension);
        if(newWorld == null){
            NailedLog.severe(new NullPointerException(), "Could not link entity %s to map %d, world object was not found", entity, toDimension);
            if(entity instanceof ICommandSender) ((ICommandSender) entity).sendChatToPlayer(ChatMessageComponent.createFromText("Could not teleport you to the map, it\'s world object was null. Look at the server log for more info").setColor(EnumChatFormatting.RED));
            return;
        }
        if(coords == null){
            coords = newWorld.getSpawnPoint();
            options.setCoordinates(coords);
        }
        TeleportEvent.TeleportEventAlter event = new TeleportEvent.TeleportEventAlter(from, newWorld, entity, options);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.spawn != null) coords = event.spawn;
        if(event.rotationYaw != null) yaw = event.rotationYaw;
        teleportEntity(newWorld, entity, toDimension, coords, yaw, options);
    }

    private static Entity teleportEntity(World newWorld, Entity entity, int toDimension, ChunkCoordinates spawnCoords, float yaw, TeleportOptions options){
        if(!TeleportEventFactory.isTeleportationPermitted(entity.worldObj, entity, options)) return null;
        Entity mount = entity.ridingEntity;
        EntityPlayerMP player = null;
        if(mount != null){
            entity.mountEntity(null);
            mount = teleportEntity(newWorld, entity, toDimension, spawnCoords, yaw, options);
        }
        boolean changingWorlds = entity.worldObj != newWorld;
        TeleportEventFactory.onStartTeleport(entity.worldObj, entity, options);
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP){
            player = (EntityPlayerMP) entity;
            player.closeScreen();
            if(changingWorlds){
                player.dimension = toDimension;
                player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension, (byte) player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld.getHeight(), player.theItemInWorldManager.getGameType()));
                if(newWorld.provider instanceof NailedWorldProvider){
                    //TODO: send world data //NetworkHelper.sendMapData(newWorld, player, destination.getID());
                }
                ((WorldServer) entity.worldObj).getPlayerManager().removePlayer(player);
            }
        }
        if(changingWorlds){
            removeEntityFromWorld(entity.worldObj, entity, true);
        }
        TeleportEventFactory.onExitWorld(entity, options);
        //FIXME
        /////entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, yaw, entity.rotationPitch);
        /////((WorldServer) newWorld).theChunkProviderServer.loadChunk(spawnCoords.posX >> 4, spawnCoords.posZ >> 4);
        //while(getCollidingWorldGeometry(newWorld, entity).size() != 0){
        //    spawnCoords.posY ++;
        //    entity.setPosition(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D);
        //}
        if(changingWorlds){
            if(entity instanceof EntityPlayerMP){
                player = (EntityPlayerMP) player;
                entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, yaw, entity.rotationPitch);
                ((WorldServer) newWorld).theChunkProviderServer.loadChunk(spawnCoords.posX >> 4, spawnCoords.posZ >> 4);
                entity.setPosition(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D);
            }
        }
        if(changingWorlds){
            if(!(entity instanceof EntityPlayer)){
                NBTTagCompound entityNBT = new NBTTagCompound();
                entity.isDead = false;
                entity.writeToNBTOptional(entityNBT);
                entity.isDead = true;
                entity = EntityList.createEntityFromNBT(entityNBT, newWorld);
                if(entity == null) return null;
                entity.dimension = newWorld.provider.dimensionId;
            }
            newWorld.spawnEntityInWorld(entity);
            entity.setWorld(newWorld);
        }
        if(changingWorlds && entity instanceof EntityPlayer){
            entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, yaw, entity.rotationPitch);
        }
        TeleportEventFactory.onEnterWorld((WorldServer) newWorld, entity, options);
        newWorld.updateEntityWithOptionalForce(entity, false);
        if(changingWorlds && entity instanceof EntityPlayer){
            entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, yaw, entity.rotationPitch);
        }
        if(entity instanceof EntityPlayerMP){
            player = (EntityPlayerMP) entity;
            if(changingWorlds) player.mcServer.getConfigurationManager().func_72375_a(player, (WorldServer) newWorld);
            //((WorldServer) entity.worldObj).getPlayerManager().addPlayer(player);
            player.playerNetServerHandler.setPlayerLocation(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
        }
        newWorld.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP && changingWorlds){
            player = (EntityPlayerMP) entity;
            player.theItemInWorldManager.setWorld((WorldServer) newWorld);
            player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) newWorld);
            player.mcServer.getConfigurationManager().syncPlayerInventory(player);
            for(PotionEffect effect : (Iterable<PotionEffect>) player.getActivePotionEffects()){
                player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
            }
            player.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(player.experience, player.experienceTotal, player.experienceLevel));
        }
        if(entity instanceof EntityPlayerMP){
            entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, yaw, entity.rotationPitch);
        }
        TeleportEventFactory.onEndTeleport((WorldServer) newWorld, entity, options);
        if(mount != null){
            if(entity instanceof EntityPlayerMP){
                newWorld.updateEntityWithOptionalForce(entity, true);
            }
            entity.mountEntity(mount);
        }
        return entity;
    }

    private static void removeEntityFromWorld(World world, Entity entity, boolean directlyRemove){
        if(entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            player.closeScreen();
            world.playerEntities.remove(player);
            world.updateAllPlayersSleepingFlag();
            int chunkX = entity.chunkCoordX;
            int chunkZ = entity.chunkCoordZ;
            if(entity.addedToChunk && world.getChunkProvider().chunkExists(chunkX, chunkZ)){
                world.getChunkFromChunkCoords(chunkX, chunkZ).removeEntity(entity);
                world.getChunkFromChunkCoords(chunkX, chunkZ).isModified = true;
            }
            if(directlyRemove){
                world.loadedEntityList.remove(entity);
                world.onEntityRemoved(entity);
            }
        }
        entity.isDead = true;
    }

    private static List<AxisAlignedBB> getCollidingWorldGeometry(World world, Entity entity){
        List<AxisAlignedBB> ret = Lists.newArrayList();
        AxisAlignedBB bb = entity.boundingBox;
        int x1 = MathHelper.floor_double(bb.minX);
        int x2 = MathHelper.floor_double(bb.maxX + 1);
        int y1 = MathHelper.floor_double(bb.minY);
        int y2 = MathHelper.floor_double(bb.maxY + 1);
        int z1 = MathHelper.floor_double(bb.minZ);
        int z2 = MathHelper.floor_double(bb.maxZ + 1);
        for(int x = x1; x < x2; x++){
            for(int z = z1; z < z2; z++){
                if(world.blockExists(x, 64, z)){
                    for(int y = y1 - 1; y < y2; y++){
                        Block block = Block.blocksList[world.getBlockId(x, y, z)];
                        if(block != null){
                            block.addCollisionBoxesToList(world, x, y, z, bb, ret, entity);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
