package jk_5.nailed;

import java.io.*;
import java.text.*;
import java.util.*;

import com.google.gson.*;

import org.apache.commons.io.*;

import net.minecraft.entity.player.*;
import net.minecraft.server.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.relauncher.*;

import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.permissions.api.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.events.*;
import jk_5.nailed.api.plugin.*;
import jk_5.nailed.api.plugin.internal.*;
import jk_5.nailed.api.plugin.java.*;
import jk_5.nailed.blocks.*;
import jk_5.nailed.camera.*;
import jk_5.nailed.chat.joinmessage.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.irc.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.mappack.*;
import jk_5.nailed.map.stat.*;
import jk_5.nailed.map.teleport.*;
import jk_5.nailed.network.*;
import jk_5.nailed.permissions.*;
import jk_5.nailed.permissions.zone.*;
import jk_5.nailed.permissions.zone.types.*;
import jk_5.nailed.players.*;
import jk_5.nailed.scheduler.*;
import jk_5.nailed.server.command.*;
import jk_5.nailed.util.*;
import jk_5.nailed.util.invsee.*;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = NailedServer.modid, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84")
public class NailedServer {

    public static final String COMMANDBLOCK_PERMISSION = "minecraft.commandBlock.edit";

    protected static final String modid = "Nailed";
    private static JsonObject config;

    private static IrcBot ircBot;
    private static NailedPermissionFactory permissionFactory;

    private static PluginManager pluginManager = new DefaultPluginManager();

    public NailedServer() {
        if(FMLLaunchHandler.side().isClient()){
            throw new RuntimeException("Nailed-Server is server-only, don\'t use it on the client!");
        }

        NailedAPI.setMapLoader(new NailedMapLoader());
        NailedAPI.setMappackLoader(new NailedMappackLoader());
        NailedAPI.setPlayerRegistry(new NailedPlayerRegistry());
        NailedAPI.setScheduler(new NailedScheduler());
        NailedAPI.setTeleporter(new NailedTeleporter());
        NailedAPI.setZoneRegistry(new NailedZoneRegistry());
        NailedAPI.setMovementHandler(new MovementHandler());
        NailedAPI.setCommandRegistry((NailedCommandManager) MinecraftServer.getServer().getCommandManager());

        FMLCommonHandler.instance().bus().register(NailedAPI.getScheduler());

        this.loadPlugins();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
            }finally{
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(pw);
            }
        }

        NailedLog.info("Creating config file");
        FileReader fr = null;
        try{
            fr = new FileReader(configFile);
            config = (JsonObject) new JsonParser().parse(fr);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }finally{
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

        NailedLog.info("Initializing network pipeline");
        NailedNetworkHandler.registerChannel();

        NailedLog.info("Registering event handlers");
        IpcEventListener ipcEventListener = new IpcEventListener();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(NailedAPI.getPlayerRegistry());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMapLoader());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMappackLoader());
        MinecraftForge.EVENT_BUS.register(new StatEventHandler());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerForge());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerEffect());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerMotion());
        MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
        MinecraftForge.EVENT_BUS.register(ipcEventListener);
        MinecraftForge.EVENT_BUS.register(new LoggingCommandListener());
        FMLCommonHandler.instance().bus().register(NailedAPI.getPlayerRegistry());
        FMLCommonHandler.instance().bus().register(NailedAPI.getMapLoader());
        FMLCommonHandler.instance().bus().register(new InvSeeTicker());
        FMLCommonHandler.instance().bus().register(ipcEventListener);

        FMLCommonHandler.instance().registerCrashCallable(new SchedulerCrashCallable());

        NailedLog.info("Registering blocks");
        NailedBlocks.init();

        NailedLog.info("Registering permissionmanager");
        permissionFactory = new NailedPermissionFactory();
        PermissionsManager.setPermFactory(permissionFactory, NailedServer.modid);

        ircBot = new IrcBot();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.post(new RegisterStatTypeEvent(StatTypeManager.instance().getStatTypes()));

        NailedLog.info("Registering permissions");
        JoinMessageSender.registerPermissions();
        PermissionsManager.registerPermission(COMMANDBLOCK_PERMISSION, RegisteredPermValue.OP);

        NailedLog.info("Registering zone types");
        NailedAPI.getZoneRegistry().registerZones();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        NailedLog.info("Loading the mappacks");
        NailedAPI.getMappackLoader().loadMappacks(null);

        ircBot.connect();
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        IpcManager.instance().start();

        NailedAPI.getZoneRegistry().lockZones();
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        PermissionsManager.addPermissionsToFactory();

        NailedLog.info("Reading permission config");
        permissionFactory.readConfig();

        ((NailedMappackLoader) NailedAPI.getMappackLoader()).loadASync = true;
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if(event.entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.entity;
            player.foodStats = new NailedFoodStats();
        }
    }

    @SubscribeEvent
    public void onZoneRegistry(RegisterZoneEvent event) {
        event.registerZoneType("Square", new SquareZoneType());
        event.registerZoneType("Cube", new CubeZoneType());
        event.registerZoneType("Circle", new CircleZoneType());
        event.registerZoneType("Sphere", new SphereZoneType());
    }

    public static JsonObject getConfig() {
        return NailedServer.config;
    }

    public void loadPlugins() {
        pluginManager.registerLoader(JavaPluginLoader.class);
        pluginManager.registerLoader(InternalPluginLoader.class);

        File pluginFolder = new File("plugins");

        if(pluginFolder.exists()){
            Plugin[] plugins = pluginManager.loadPlugins(pluginFolder);
            for(Plugin plugin : plugins){
                try{
                    plugin.getLogger().info("Loading plugin " + plugin.getName()); //TODO: replace .getName() with .getMetadata().getName()
                    plugin.onLoad();
                }catch(Throwable ex){
                    NailedLog.warn(ex.getMessage() + " initializing " + plugin.getName(), ex);
                }
            }
        }else{
            pluginFolder.mkdir();
        }
    }

    @NetworkCheckHandler
    public boolean accepts(Map<String, String> mods, Side side) {
        return true;
    }
}
