package jk_5.nailed.map.teleport;

import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.demo.DemoWorldManager;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraftforge.common.MinecraftForge;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.teleport.TeleportEvent;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.map.teleport.Teleporter;
import jk_5.nailed.api.player.ClientType;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;
import jk_5.nailed.players.TeamUndefined;

public class NailedTeleporter implements Teleporter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean teleportEntity(@Nonnull Entity entity, @Nonnull TeleportOptions options) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkNotNull(options, "options");

        Map current = NailedAPI.getMapLoader().getMap(entity.worldObj);
        Map destination = options.getDestination();
        if(destination == null){
            destination = current;
        }
        World destWorld = destination.getWorld();
        if(destWorld.isRemote){
            return false;
        }
        options = options.reMake(); //We don't want to accidently modify the options object passed in, so we copy it.
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

    private static Entity teleportEntity(Map currentMap, Map destMap, Entity entity, Location location, TeleportOptions options) {
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
                Player np = NailedAPI.getPlayerRegistry().getPlayer(player);
                if(np.getClientType() == ClientType.NAILED || np.getClientType() == ClientType.FORGE){
                    player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, destWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                }else{
                    player.playerNetServerHandler.sendPacket(new S07PacketRespawn(1, player.worldObj.difficultySetting, destWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                    player.playerNetServerHandler.sendPacket(new S07PacketRespawn(0, player.worldObj.difficultySetting, destWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
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
                if(entity == null){
                    return null;
                }
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
            if(changingworlds){
                player.mcServer.getConfigurationManager().func_72375_a(player, destWorld);
            }
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

    private static void removeEntityFromWorld(World world, Entity entity) {
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
        }else{
            world.removeEntity(entity);
            world.onEntityRemoved(entity);
        }
    }

    @Override
    public EntityPlayerMP respawnPlayer(EntityPlayerMP entity, int dimension, boolean finishedEnd) {
        MinecraftServer server = MinecraftServer.getServer();
        Player player = NailedAPI.getPlayerRegistry().getPlayer(entity);
        Map destMap = NailedAPI.getMapLoader().getMap(dimension);
        Map currentMap = NailedAPI.getMapLoader().getMap(entity.getServerForPlayer());
        if(destMap == null){
            destMap = NailedAPI.getMapLoader().getLobby();
        }

        currentMap.getWorld().getEntityTracker().removePlayerFromTrackers(entity);
        currentMap.getWorld().getEntityTracker().removeEntityFromAllTrackingPlayers(entity);
        currentMap.getWorld().getPlayerManager().removePlayer(entity);
        server.getConfigurationManager().playerEntityList.remove(entity);
        server.worldServerForDimension(entity.dimension).removePlayerEntityDangerously(entity); //Force the entity to be removed from it's current dimension

        Location pos;
        Mappack mappack = destMap.getMappack();
        if(mappack == null){
            pos = new Location(0, 64, 0);
        }else{
            pos = mappack.getMappackMetadata().getSpawnPoint();
        }
        if(destMap.getGameManager().isGameRunning()){
            if(player.getSpawnpoint() != null){
                pos = player.getSpawnpoint();
            }else if(player.getTeam() instanceof TeamUndefined){
                if(mappack != null && mappack.getMappackMetadata().isChoosingRandomSpawnpointAtRespawn()){
                    List<Location> spawnpoints = mappack.getMappackMetadata().getRandomSpawnpoints();
                    pos = spawnpoints.get(NailedAPI.getMapLoader().getRandomSpawnpointSelector().nextInt(spawnpoints.size()));
                }
            }else{
                if(player.getTeam().shouldOverrideDefaultSpawnpoint()){
                    pos = player.getTeam().getSpawnpoint();
                }
            }
        }

        entity.dimension = dimension;

        ItemInWorldManager worldManager = server.isDemo() ? new DemoWorldManager(destMap.getWorld()) : new ItemInWorldManager(destMap.getWorld());

        EntityPlayerMP newPlayer = new EntityPlayerMP(server, destMap.getWorld(), player.getGameProfile(), worldManager);
        newPlayer.playerNetServerHandler = entity.playerNetServerHandler;
        newPlayer.clonePlayer(entity, finishedEnd);
        newPlayer.dimension = dimension;
        newPlayer.setEntityId(entity.getEntityId());

        worldManager.setGameType(entity.theItemInWorldManager.getGameType());
        worldManager.initializeGameType(destMap.getWorld().getWorldInfo().getGameType());

        newPlayer.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
        destMap.getWorld().theChunkProviderServer.loadChunk((int) newPlayer.posX >> 4, (int) newPlayer.posZ >> 4);

        if(player.getClientType() == ClientType.NAILED || player.getClientType() == ClientType.FORGE){
            player.sendPacket(new S07PacketRespawn(newPlayer.dimension, destMap.getWorld().difficultySetting, destMap.getWorld().getWorldInfo().getTerrainType(), worldManager.getGameType()));
        }else{
            player.sendPacket(new S07PacketRespawn(0, destMap.getWorld().difficultySetting, destMap.getWorld().getWorldInfo().getTerrainType(), worldManager.getGameType()));
        }
        player.getNetHandler().setPlayerLocation(pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
        player.sendPacket(new S05PacketSpawnPosition(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()));
        player.sendPacket(new S1FPacketSetExperience(newPlayer.experience, newPlayer.experienceTotal, newPlayer.experienceLevel));
        server.getConfigurationManager().updateTimeAndWeatherForPlayer(newPlayer, destMap.getWorld());
        destMap.getWorld().getPlayerManager().addPlayer(newPlayer);
        destMap.getWorld().spawnEntityInWorld(newPlayer);
        //noinspection unchecked
        server.getConfigurationManager().playerEntityList.add(newPlayer);
        newPlayer.addSelfToInternalCraftingInventory();
        newPlayer.setHealth(newPlayer.getHealth());

        FMLCommonHandler.instance().firePlayerRespawnEvent(newPlayer);
        return newPlayer;
    }
}
