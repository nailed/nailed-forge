package jk_5.nailed.map.teleport;

import com.google.common.base.Preconditions;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.teleport.TeleportEvent;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.map.teleport.Teleporter;
import jk_5.nailed.map.Location;
import jk_5.nailed.map.gen.NailedWorldProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

/**
 * {@inheritDoc}
 */
public class NailedTeleporter implements Teleporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean teleportEntity(@Nonnull Entity entity, @Nonnull TeleportOptions options){
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkNotNull(options, "options");

        Map current = NailedAPI.getMapLoader().getMap(entity.worldObj);
        Map destination = options.getDestination();
        if(destination == null){
            destination = current;
        }
        World destWorld = destination.getWorld();
        if(destWorld.isRemote) return false;
        options = options.reMake(); //We don't want to accidently modify the options object passed in, so we clone it.
        Location location = options.getLocation();
        if(!TeleportEventFactory.isLinkPermitted(current, destination, entity, options)){
            return false;
        }
        if(location == null){
            location = new Location(destWorld.getSpawnPoint());
            options.setLocation(location);
        }
        TeleportEvent.TeleportEventAlter event = new TeleportEvent.TeleportEventAlter(current, destination, entity, options.reMake());
        MinecraftForge.EVENT_BUS.post(event);
        if(event.location != null){
            location = event.location;
        }
        teleportEntity(current, destination, entity, location, options);
        return true;
    }

    private static Entity teleportEntity(Map currentMap, Map destMap, Entity entity, Location location, TeleportOptions options){
        int dimension = destMap.getID();
        WorldServer destWorld = destMap.getWorld();
        if(!TeleportEventFactory.isLinkPermitted(currentMap, destMap, entity, options)){
            return null;
        }
        Entity mount = entity.ridingEntity;
        if(entity.ridingEntity != null){
            entity.mountEntity(null);
            mount = teleportEntity(currentMap, destMap, mount, location, options);
        }
        double mX = entity.motionX;
        double mY = entity.motionY;
        double mZ = entity.motionZ;
        boolean changingworlds = entity.worldObj != destWorld;
        TeleportEventFactory.onLinkStart(currentMap, destMap, entity, options);
        entity.worldObj.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP){
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

        entity.setLocationAndAngles(location.getX(), location.getX(), location.getZ(), location.getYaw(), location.getPitch());
        destWorld.theChunkProviderServer.loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
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
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        TeleportEventFactory.onEnterWorld(currentMap, destMap, entity, options);
        destWorld.updateEntityWithOptionalForce(entity, false);
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if(entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if(changingworlds) player.mcServer.getConfigurationManager().func_72375_a(player, destWorld);
            player.playerNetServerHandler.setPlayerLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }
        destWorld.updateEntityWithOptionalForce(entity, false);
        if(entity instanceof EntityPlayerMP && changingworlds){
            EntityPlayerMP player = (EntityPlayerMP) entity;
            player.theItemInWorldManager.setWorld(destWorld);
            player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, destWorld);
            player.mcServer.getConfigurationManager().syncPlayerInventory(player);

            for(Object obj : player.getActivePotionEffects()){
                player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), (PotionEffect) obj));
            }
            player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
        }
        entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        TeleportEventFactory.onLinkEnd(currentMap, destMap, entity, options);
        if(options.isMaintainMomentum()){
            entity.motionX = mX;
            entity.motionY = mY;
            entity.motionZ = mZ;
        }
        if(mount != null){
            if(entity instanceof EntityPlayerMP){
                destWorld.updateEntityWithOptionalForce(entity, true);
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
            int x = entity.chunkCoordX;
            int z = entity.chunkCoordZ;
            if(entity.addedToChunk && world.getChunkProvider().chunkExists(x, z)){
                Chunk chunk = world.getChunkFromChunkCoords(x, z);
                chunk.removeEntity(entity);
                chunk.isModified = true;
            }
            world.loadedEntityList.remove(entity);
            world.onEntityRemoved(entity);
        }
        entity.isDead = false;
    }
}
