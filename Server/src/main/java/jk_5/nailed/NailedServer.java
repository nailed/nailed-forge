package jk_5.nailed;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import jk_5.nailed.achievement.AchievementEventListener;
import jk_5.nailed.achievement.NailedAchievements;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.chat.joinmessage.JoinMessageSender;
import jk_5.nailed.ipc.IpcEventListener;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.irc.IrcBot;
import jk_5.nailed.item.NailedItems;
import jk_5.nailed.map.NailedMapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.map.mappack.NailedMappackLoader;
import jk_5.nailed.map.stat.RegisterStatTypeEvent;
import jk_5.nailed.map.stat.StatEventHandler;
import jk_5.nailed.map.stat.StatTypeManager;
import jk_5.nailed.map.teleport.NailedTeleporter;
import jk_5.nailed.map.teleport.TeleportEventListenerEffect;
import jk_5.nailed.map.teleport.TeleportEventListenerForge;
import jk_5.nailed.map.teleport.TeleportEventListenerMotion;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.permissions.NailedPermissionFactory;
import jk_5.nailed.permissions.PermissionEventHandler;
import jk_5.nailed.permissions.zone.NailedZoneRegistry;
import jk_5.nailed.players.NailedPlayerRegistry;
import jk_5.nailed.scheduler.NailedScheduler;
import jk_5.nailed.scheduler.SchedulerCrashCallable;
import jk_5.nailed.server.command.LoggingCommandListener;
import jk_5.nailed.server.command.NailedCommandManager;
import jk_5.nailed.util.MotdManager;
import jk_5.nailed.util.NailedFoodStats;
import jk_5.nailed.util.invsee.InvSeeTicker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = NailedServer.modid, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84")
public class NailedServer {

    protected static final String modid = "Nailed";
    private static JsonObject config;
    private static int providerID;

    private static IrcBot ircBot;
    private static NailedPermissionFactory permissionFactory;

    public static final String COMMANDBLOCK_PERMISSION = "minecraft.commandBlock.edit";

    public NailedServer(){
        if(FMLLaunchHandler.side().isClient()){
            throw new RuntimeException("Nailed-Server is server-only, don\'t use it on the client!");
        }

        NailedAPI.setMapLoader(new NailedMapLoader());
        NailedAPI.setMappackLoader(new NailedMappackLoader());
        NailedAPI.setPlayerRegistry(new NailedPlayerRegistry());
        NailedAPI.setScheduler(new NailedScheduler());
        NailedAPI.setTeleporter(new NailedTeleporter());
        NailedAPI.setZoneRegistry(new NailedZoneRegistry());

        FMLCommonHandler.instance().bus().register(NailedAPI.getScheduler());

        MinecraftServer.getServer().commandManager = new NailedCommandManager();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        File configDir = new File(event.getModConfigurationDirectory(), "nailed");
        configDir.mkdirs();

        File configFile = new File(configDir, "config.json");
        if(!configFile.exists()){
            NailedLog.info("Loading default config file");
            InputStream is = null;
            PrintWriter pw = null;
            try{
                is = NailedServer.class.getResourceAsStream("/assets/nailed/config.json");
                pw = new PrintWriter(configFile);
                IOUtils.copy(is, pw);
            }catch(Exception e){
                NailedLog.fatal("Error while creating default config file", e);
            }finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(pw);
            }
        }

        NailedLog.info("Creating config file");
        FileReader fr = null;
        try {
            fr = new FileReader(configFile);
            config = (JsonObject) new JsonParser().parse(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(fr);
        }

        if(NailedAPI.getMapLoader().getMapsFolder().exists()){
            NailedLog.info("Clearing away old maps folder");
            new File(".", "mapbackups").mkdirs();
            File dest = new File(new File(".", "mapbackups"), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            NailedAPI.getMapLoader().getMapsFolder().renameTo(dest);
        }

        NailedLog.info("Loading join message");
        JoinMessageSender.readConfig(configDir);
        
        NailedLog.info("Loading achievements");
        NailedAchievements.addAchievements();

        NailedLog.info("Initializing network pipeline");
        NailedNetworkHandler.registerChannel();

        NailedLog.info("Registering event handlers");
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(NailedAPI.getPlayerRegistry());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMapLoader());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMappackLoader());
        MinecraftForge.EVENT_BUS.register(new AchievementEventListener());
        MinecraftForge.EVENT_BUS.register(new StatEventHandler());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerForge());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerEffect());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerMotion());
        MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
        MinecraftForge.EVENT_BUS.register(new IpcEventListener());
        MinecraftForge.EVENT_BUS.register(new LoggingCommandListener());
        FMLCommonHandler.instance().bus().register(NailedAPI.getPlayerRegistry());
        FMLCommonHandler.instance().bus().register(NailedAPI.getMapLoader());
        FMLCommonHandler.instance().bus().register(new InvSeeTicker());
        FMLCommonHandler.instance().bus().register(new MotdManager());

        FMLCommonHandler.instance().registerCrashCallable(new SchedulerCrashCallable());

        NailedLog.info("Registering blocks");
        NailedBlocks.init();
        NailedItems.init();

        NailedLog.info("Registering Nailed WorldProvider");
        NailedServer.providerID = config.get("providerId").getAsInt();
        DimensionManager.registerProviderType(NailedServer.providerID, NailedWorldProvider.class, false);

        NailedLog.info("Overriding Default WorldProviders");
        DimensionManager.unregisterProviderType(-1);
        DimensionManager.unregisterProviderType(0);
        DimensionManager.unregisterProviderType(1);
        DimensionManager.registerProviderType(-1, NailedWorldProvider.class, false);
        DimensionManager.registerProviderType(0, NailedWorldProvider.class, true);
        DimensionManager.registerProviderType(1, NailedWorldProvider.class, false);
        DimensionManager.unregisterDimension(-1);
        DimensionManager.unregisterDimension(1);

        NailedLog.info("Registering permissionmanager");
        permissionFactory = new NailedPermissionFactory();
        PermissionsManager.setPermFactory(permissionFactory, NailedServer.modid);

        ircBot = new IrcBot();
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.post(new RegisterStatTypeEvent(StatTypeManager.instance().getStatTypes()));

        NailedLog.info("Registering achievements");
        NailedAchievements.init();

        NailedLog.info("Registering permissions");
        JoinMessageSender.registerPermissions();
        PermissionsManager.registerPermission(COMMANDBLOCK_PERMISSION, RegisteredPermValue.OP);

        NailedLog.info("Registering zone types");
        NailedAPI.getZoneRegistry().registerZones();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        NailedLog.info("Loading the mappacks");
        NailedAPI.getMappackLoader().loadMappacks(null);

        ircBot.connect();
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event){
        IpcManager.instance().start();

        NailedAPI.getZoneRegistry().lockZones();
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event){
        PermissionsManager.addPermissionsToFactory();

        NailedLog.info("Reading permission config");
        permissionFactory.readConfig();

        ((NailedMappackLoader) NailedAPI.getMappackLoader()).loadASync = true;
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.entity;
            player.foodStats = new NailedFoodStats();
        }
    }

    public static JsonObject getConfig() {
        return NailedServer.config;
    }

    public static int getProviderID() {
        return NailedServer.providerID;
    }
}
