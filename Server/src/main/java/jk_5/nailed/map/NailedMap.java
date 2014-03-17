package jk_5.nailed.map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.NailedLog;
import jk_5.nailed.NailedServer;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.GameManager;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.map.game.NailedGameManager;
import jk_5.nailed.map.scoreboard.NailedScoreboardManager;
import jk_5.nailed.map.script.FileSystemException;
import jk_5.nailed.map.script.MachineRegistry;
import jk_5.nailed.map.script.ServerMachine;
import jk_5.nailed.map.script.Terminal;
import jk_5.nailed.map.sign.SignCommandHandler;
import jk_5.nailed.map.stat.StatManager;
import jk_5.nailed.map.weather.WeatherController;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedMap implements Map {

    @Getter private int ID;
    @Getter private final Mappack mappack;
    @Getter private World world;
    @Getter private boolean isLoaded = false;
    @Getter private final TeamManager teamManager;
    @Getter private final StatManager statManager;
    @Getter private boolean dataResyncRequired = true;
    @Getter private WeatherController weatherController;
    @Getter private SignCommandHandler signCommandHandler;
    @Getter private GameManager gameManager;
    @Getter private NailedScoreboardManager scoreboardManager;
    @Getter private List<Player> players = Lists.newArrayList();

    @Getter private ServerMachine machine;
    public IMount mappackMount;
    public boolean mounted = false;

    public NailedMap(Mappack mappack, int id){
        this.ID = id;
        this.mappack = mappack;
        this.teamManager = new TeamManager(this);
        this.statManager = new StatManager(this);
        this.weatherController = new WeatherController(this);
        this.signCommandHandler = new SignCommandHandler(this);
        this.gameManager = new NailedGameManager(this);
        this.scoreboardManager = new NailedScoreboardManager(this);

        NailedAPI.getMapLoader().registerMap(this);

        if(this.mappack == null){
            this.mounted = true; //Don't try to mount anything when we don't have a mappack
        }
    }

    public void initMapServer(){
        if(this.isLoaded) return;
        NailedLog.info("Initializing %s", this.getSaveFileName());

        DimensionManager.registerDimension(this.getID(), NailedServer.getProviderID());
        DimensionManager.initDimension(this.getID());

        ForgeMessage.DimensionRegisterMessage packet = new ForgeMessage.DimensionRegisterMessage(this.getID(), NailedServer.getProviderID());
        EmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channel.writeOutbound(packet);
    }

    @Override
    public void setWorld(World world){
        Preconditions.checkNotNull(world);
        this.world = world;
        if(world.provider != null) this.ID = world.provider.dimensionId;
        this.isLoaded = true;
        this.teamManager.onWorldSet();

        if(this.mappack != null){
            MappackMetadata meta = this.mappack.getMappackMetadata();
            GameRules rules = world.getGameRules();
            for(java.util.Map.Entry<String, String> e : meta.getGameruleConfig().entrySet()){
                if(rules.hasRule(e.getKey())){
                    rules.setOrCreateGameRule(e.getKey(), e.getValue());
                }
            }
            world.difficultySetting = meta.getDifficulty();                                 //TODO: is this correct?
            world.setAllowedSpawnTypes(meta.isSpawnHostileMobs() && world.difficultySetting.getDifficultyId() > 0, meta.isSpawnFriendlyMobs());
        }

        this.machine = new ServerMachine(world, MachineRegistry.getNextId(), ServerMachine.REGISTRY.getUnusedInstanceID(), Terminal.WIDTH, Terminal.HEIGHT);
        this.machine.setPreferredSaveDir(new File(this.getSaveFolder(), "machine"));
        ServerMachine.REGISTRY.add(this.machine.getId(), this.machine);
        this.machine.turnOn();

        NailedLog.info("Registered map " + this.getSaveFileName());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void unloadAndRemove(){
        NailedAPI.getMapLoader().removeMap(this);
        this.getSaveFolder().delete();
    }

    @Override
    public void reloadFromMappack(){
        for(Player player : this.getPlayers()){
            player.getEntity().playerNetServerHandler.kickPlayerFromServer("[" + ChatColor.GREEN + "Nailed" + ChatColor.RESET + "] Reloading the map you were in");
        }
        this.unloadAndRemove();
        this.mappack.prepareWorld(this.getSaveFolder());
        DimensionManager.registerDimension(this.getID(), NailedServer.getProviderID());
        DimensionManager.initDimension(this.getID());
    }

    @Override
    public void onPlayerJoined(Player player){
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.TimeUpdate(true, ""), player.getEntity());
        this.scoreboardManager.onPlayerJoinedMap(player);
        this.teamManager.onPlayerJoinedMap(player);
        this.players.add(player);
        NailedMapLoader.instance().checkShouldStart(this);
    }

    @Override
    public void onPlayerLeft(Player player){
        this.scoreboardManager.onPlayerLeftMap(player);
        this.teamManager.onPlayerLeftMap(player);
        this.players.remove(player);
        NailedMapLoader.instance().checkShouldStart(this);
    }

    @Override
    public void onTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            if(this.machine != null){
                this.machine.update();
                if(!this.mounted && this.machine.getApiEnvironment().getFileSystem() != null){
                    if(this.mappackMount == null && this.mappack != null){
                        this.mappackMount = this.mappack.createMount();
                        if(this.mappackMount != null){
                            try{
                                this.machine.getApiEnvironment().getFileSystem().mount("mappack", "mappack", this.mappackMount);
                            }catch(FileSystemException e){
                                NailedLog.error(e, "Error while mounting mappack folder to machine\'s filesystem");
                            }
                        }
                        this.mounted = true;
                    }
                }
            }
        }
    }

    public String getSaveFileName(){
        return PotentialMap.getSaveFileName(this);
    }

    public File getSaveFolder(){
        return new File(NailedMapLoader.instance().getMapsFolder(), this.getSaveFileName());
    }

    public TeleportOptions getSpawnTeleport(){
        if(this.mappack == null){
            return new TeleportOptions(this, new Spawnpoint(this.world.getSpawnPoint()), 0, 0);
        }
        MappackMetadata meta = this.mappack.getMappackMetadata();
        Spawnpoint spawnpoint = new Spawnpoint(meta.getSpawnPoint());
        return new TeleportOptions(this, spawnpoint, spawnpoint.yaw, spawnpoint.pitch);
    }

    public void broadcastChatMessage(IChatComponent message){
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.getCurrentMap() == this){
                player.sendChat(message);
            }
        }
    }

    public void broadcastChatMessage(String message){
        this.broadcastChatMessage(new ChatComponentText(message));
    }

    @Override
    public void onGameStarted(){
        this.teamManager.onGameStarted();
    }

    @Override
    public void onGameEnded(){
        this.teamManager.onGameEnded();
    }

    @Override
    public Spawnpoint getRandomSpawnpoint(){
        List<Spawnpoint> spawnpoints = mappack.getMappackMetadata().getRandomSpawnpoints();
        if(spawnpoints.size() == 0) return null;
        return spawnpoints.get(NailedMapLoader.instance().getRandomSpawnpointSelector().nextInt(spawnpoints.size()));
    }

    public void markDataNeedsResync(){
        this.dataResyncRequired = true;
    }

    public void onSynced(){
        this.dataResyncRequired = false;
    }

    @Override
    public void broadcastNotification(String msg){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.Notification(msg, null, 0xFFFFFF), this.getID());
    }

    @Override
    public void broadcastPacket(Packet packet){
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.getID());
    }

    @Override
    public int getAmountOfPlayers(){
        return this.players.size();
    }
}
