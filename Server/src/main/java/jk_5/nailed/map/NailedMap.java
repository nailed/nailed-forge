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
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.api.zone.ZoneManager;
import jk_5.nailed.map.game.NailedGameManager;
import jk_5.nailed.map.scoreboard.NailedScoreboardManager;
import jk_5.nailed.map.script.FileSystemException;
import jk_5.nailed.map.script.MachineRegistry;
import jk_5.nailed.map.script.ServerMachine;
import jk_5.nailed.map.script.Terminal;
import jk_5.nailed.map.sign.SignCommandHandler;
import jk_5.nailed.map.stat.StatManager;
import jk_5.nailed.map.weather.WeatherController;
import jk_5.nailed.permissions.zone.DefaultZoneManager;
import jk_5.nailed.util.NailedFoodStats;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
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

    private int ID;
    private final Mappack mappack;
    private WorldServer world;
    private boolean isLoaded = false;
    private final TeamManager teamManager;
    private final StatManager statManager;
    private boolean dataResyncRequired = true;
    private WeatherController weatherController;
    private SignCommandHandler signCommandHandler;
    private GameManager gameManager;
    private NailedScoreboardManager scoreboardManager;
    private DefaultZoneManager zoneManager;
    private List<Player> players = Lists.newArrayList();
    private LocationHandler locationHandler;

    private ServerMachine machine;
    public IMount mappackMount;
    public boolean mounted = false;

    private boolean loadNextTick = false;

    public NailedMap(Mappack mappack, int id){
        this.ID = id;
        this.mappack = mappack;
        this.teamManager = new TeamManager(this);
        this.statManager = new StatManager(this);
        this.weatherController = new WeatherController(this);
        this.signCommandHandler = new SignCommandHandler(this);
        this.gameManager = new NailedGameManager(this);
        this.scoreboardManager = new NailedScoreboardManager(this);
        this.zoneManager = new DefaultZoneManager(this);
        this.locationHandler = new LocationHandler(this);

        NailedAPI.getMapLoader().registerMap(this);

        if(this.mappack == null){
            this.mounted = true; //Don't try to mount anything when we don't have a mappack
        }
    }

    @Override
    public void initMapServer(){
        if(this.isLoaded) return;
        NailedLog.info("Initializing {}", this.getSaveFileName());

        DimensionManager.registerDimension(this.getID(), NailedServer.getProviderID());
        DimensionManager.initDimension(this.getID());

        ForgeMessage.DimensionRegisterMessage packet = new ForgeMessage.DimensionRegisterMessage(this.getID(), NailedServer.getProviderID());
        EmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channel.writeOutbound(packet);
    }

    @Override
    public void setWorld(WorldServer world){
        Preconditions.checkNotNull(world);
        this.world = world;
        if(world.provider != null) this.ID = world.provider.dimensionId;
        this.isLoaded = true;
        this.teamManager.onWorldSet();

        this.loadNextTick = true;

        this.machine = new ServerMachine(world, MachineRegistry.getNextId(), ServerMachine.REGISTRY.getUnusedInstanceID(), Terminal.WIDTH, Terminal.HEIGHT);
        this.machine.setPreferredSaveDir(new File(this.getSaveFolder(), "machine"));
        ServerMachine.REGISTRY.add(this.machine.getId(), this.machine);
        this.machine.turnOn();

        NailedLog.info("Registered map {}", this.getSaveFileName());
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void unloadAndRemove(){
        NailedAPI.getMapLoader().removeMap(this);
        NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run() {
                getSaveFolder().delete();
            }
        });
    }

    @Override
    public void onPlayerJoined(Player player) {
        player.sendTimeUpdate("");
        this.scoreboardManager.onPlayerJoinedMap(player);
        this.teamManager.onPlayerJoinedMap(player);
        this.players.add(player);
        NailedMapLoader.instance().checkShouldStart(this);
        NailedFoodStats playerFoodStats = new NailedFoodStats();
        if (mappack != null) {
            playerFoodStats.setMinFoodLevel(mappack.getMappackMetadata().getMinFoodLevel());
            playerFoodStats.setMaxFoodLevel(mappack.getMappackMetadata().getMaxFoodLevel());
            player.setMaxHealth(mappack.getMappackMetadata().getMaxHealth());
            player.setMinHealth(mappack.getMappackMetadata().getMinHealth());
        }else{
            playerFoodStats.setMinFoodLevel(0);
            playerFoodStats.setMaxFoodLevel(15);
            player.setMinHealth(1);
            player.setMaxHealth(20);
        }
        player.getEntity().foodStats = playerFoodStats;
        player.setPlayersVisible(this.getPlayers());
        this.getMachine().queueEvent("player_join", player.getUsername());
    }

    @Override
    public void onPlayerLeft(Player player){
        this.scoreboardManager.onPlayerLeftMap(player);
        this.teamManager.onPlayerLeftMap(player);
        this.players.remove(player);
        NailedMapLoader.instance().checkShouldStart(this);
        this.getMachine().queueEvent("player_leave", player.getUsername());
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
                                NailedLog.error("Error while mounting mappack folder to machine\'s filesystem", e);
                            }
                        }
                        this.mounted = true;
                    }
                }
            }
            if(this.loadNextTick){
                //Delay this by one tick because world difficulty is set after the world load event is fired.
                if(this.mappack != null){
                    MappackMetadata meta = this.mappack.getMappackMetadata();
                    GameRules rules = world.getGameRules();
                    for(java.util.Map.Entry<String, String> e : meta.getGameruleConfig().entrySet()){
                        if(rules.hasRule(e.getKey())){
                            rules.setOrCreateGameRule(e.getKey(), e.getValue());
                        }
                    }
                    world.difficultySetting = meta.getDifficulty();
                    world.setAllowedSpawnTypes(world.difficultySetting.getDifficultyId() > 0, true);
                }
                this.loadNextTick = false;
            }
        }
    }

    @Override
    public String getSaveFileName(){
        return PotentialMap.getSaveFileName(this);
    }

    @Override
    public File getSaveFolder(){
        return new File(NailedMapLoader.instance().getMapsFolder(), this.getSaveFileName());
    }

    @Override
    public TeleportOptions getSpawnTeleport(){
        if(this.mappack == null){
            return new TeleportOptions(this, new Location(this.world.getSpawnPoint()));
        }
        MappackMetadata meta = this.mappack.getMappackMetadata();
        Location spawnpoint = new Location(meta.getSpawnPoint());
        return new TeleportOptions(this, spawnpoint);
    }

    @Override
    public void broadcastChatMessage(IChatComponent message){
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.getCurrentMap() == this){
                player.sendChat(message);
            }
        }
    }

    @Override
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
    public Location getRandomSpawnpoint(){
        List<Location> spawnpoints = mappack.getMappackMetadata().getRandomSpawnpoints();
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
    public void broadcastPacket(Packet packet){
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, this.getID());
    }

    @Override
    public int getAmountOfPlayers(){
        return this.players.size();
    }

    @Override
    public String toString(){
        return "NailedMap{" +
                "ID=" + ID +
                ", name=" + this.getSaveFileName() +
                ", mappack=" + mappack +
                ", isLoaded=" + isLoaded +
                '}';
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public Mappack getMappack() {
        return this.mappack;
    }

    @Override
    public WorldServer getWorld() {
        return this.world;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    @Override
    public StatManager getStatManager() {
        return this.statManager;
    }

    @Override
    public WeatherController getWeatherController() {
        return this.weatherController;
    }

    @Override
    public SignCommandHandler getSignCommandHandler() {
        return this.signCommandHandler;
    }

    @Override
    public GameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public NailedScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    public ServerMachine getMachine() {
        return this.machine;
    }

    public int getMaxFoodLevel(){
        return mappack.getMappackMetadata().getMaxFoodLevel();
    }

    public int getMinFoodLevel(){
        return mappack.getMappackMetadata().getMinFoodLevel();
    }

    @Override
    public ZoneManager getZoneManager() {
        return this.zoneManager;
    }

    @Override
    public void queueEvent(String event, Object... args) {
        this.getMachine().queueEvent(event, args);
    }

    @Override
    public ChatComponentText getInfoBar(){ return new ChatComponentText("{\"text\":\"\"}");}

    @Override
    public float getInfoBarProgress(){ return 1;}

    public jk_5.nailed.api.map.LocationHandler getLocationHandler(){ return this.locationHandler; }
}
