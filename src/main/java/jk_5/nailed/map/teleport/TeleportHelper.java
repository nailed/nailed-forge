package jk_5.nailed.map.teleport;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.NailedLog;
import jk_5.nailed.map.Map;
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

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@SideOnly(Side.SERVER)
public class TeleportHelper {

    private static MinecraftServer mcServer = null;

    public static void travelEntity(Map destination, Entity entity, TeleportOptions options){
        if(options == null) return;
        if(!TeleportEventFactory.isTeleportationPermitted(destination, entity, options)) return;
        if(mcServer == null) mcServer = MinecraftServer.getServer();
        if(mcServer == null) return;
        WorldServer newWorld = (WorldServer) destination.getWorld();
        if(newWorld == null){
            NailedLog.severe(new NullPointerException(), "Could not link entity %s to map %d, world object was not found", entity, destination.getID());
            if(entity instanceof ICommandSender) ((ICommandSender) entity).sendChatToPlayer(ChatMessageComponent.createFromText("Could not teleport you to the map, it\'s world object was null. Look at the server log for more info").setColor(EnumChatFormatting.RED));
        }
        teleportEntity(destination, entity, options);
    }

    private static Entity teleportEntity(Map destination, Entity entity, TeleportOptions options){
        if(options == null) return null;
        if(!TeleportEventFactory.isTeleportationPermitted(destination, entity, options)) return null;
        Entity mount = entity.ridingEntity;
        if(mount != null){
            entity.mountEntity(null);
            mount = teleportEntity(destination, mount, options);
        }
        WorldServer newWorld = (WorldServer) destination.getWorld();
        boolean changingWorlds = entity.worldObj != newWorld;
        TeleportEventFactory.onStartTeleport(entity.worldObj, entity, options);
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.closeScreen();
            if(changingWorlds){
                player.dimension = destination.getID();
                player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension, (byte) player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld.getHeight(), player.theItemInWorldManager.getGameType()));
                if(newWorld.provider instanceof NailedWorldProvider){
                    //TODO: send world data //NetworkHelper.sendMapData(newWorld, player, destination.getID());
                }
                ((WorldServer) entity.worldObj).getPlayerManager().removePlayer(player);
            }
        }
        if(changingWorlds){
            removeEntityFromWorld(entity.worldObj, entity);
        }
        TeleportEventFactory.onExitWorld(entity, options);

        ChunkCoordinates spawnCoords = options.getCoordinates();
        entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, options.getYaw(), entity.rotationPitch);
        newWorld.theChunkProviderServer.loadChunk(options.getCoordinates().posX >> 4, options.getCoordinates().posZ >> 4);
        while(getCollidingWorldGeometry(destination, entity).size() != 0){
            spawnCoords.posY ++;
            entity.setPosition(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D);
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
        entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, options.getYaw(), entity.rotationPitch);
        TeleportEventFactory.onEnterWorld(newWorld, entity, options);
        newWorld.updateEntityWithOptionalForce(entity, false);
        entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, options.getYaw(), entity.rotationPitch);
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if(changingWorlds) player.mcServer.getConfigurationManager().func_72375_a(player, newWorld);
            player.playerNetServerHandler.setPlayerLocation(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
        }
        newWorld.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP && changingWorlds){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.theItemInWorldManager.setWorld(newWorld);
            player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, newWorld);
            player.mcServer.getConfigurationManager().syncPlayerInventory(player);
            for(PotionEffect effect : (Iterable<PotionEffect>) player.getActivePotionEffects()){
                player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
            }
            player.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(player.experience, player.experienceTotal, player.experienceLevel));
        }
        entity.setLocationAndAngles(spawnCoords.posX + 0.5D, spawnCoords.posY, spawnCoords.posZ + 0.5D, options.getYaw(), entity.rotationPitch);
        TeleportEventFactory.onEndTeleport(newWorld, entity, options);
        if(mount != null){
            if(entity instanceof EntityPlayerMP){
                newWorld.updateEntityWithOptionalForce(entity, true);
            }
            entity.mountEntity(mount);
        }
        return entity;
    }

    private static void removeEntityFromWorld(World world, Entity entity){
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
            world.loadedEntityList.remove(entity);
            world.onEntityRemoved(entity);
        }
        entity.isDead = true;
    }

    private static List<AxisAlignedBB> getCollidingWorldGeometry(Map map, Entity entity){
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
                if(map.getWorld().blockExists(x, 64, z)){
                    for(int y = y1 - 1; y < y2; y++){
                        Block block = Block.blocksList[map.getWorld().getBlockId(x, y, z)];
                        if(block != null){
                            block.addCollisionBoxesToList(map.getWorld(), x, y, z, bb, ret, entity);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
