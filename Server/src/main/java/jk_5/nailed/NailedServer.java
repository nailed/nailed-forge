package jk_5.nailed;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import jk_5.nailed.achievement.AchievementEventListener;
import jk_5.nailed.achievement.NailedAchievements;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.config.ConfigFile;
import jk_5.nailed.api.events.RegisterInstructionEvent;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.irc.IrcBot;
import jk_5.nailed.map.NailedMapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.map.instruction.InstructionReader;
import jk_5.nailed.map.mappack.NailedMappackLoader;
import jk_5.nailed.map.stat.RegisterStatTypeEvent;
import jk_5.nailed.map.stat.StatEventHandler;
import jk_5.nailed.map.stat.StatTypeManager;
import jk_5.nailed.map.teleport.TeleportEventListenerEffect;
import jk_5.nailed.map.teleport.TeleportEventListenerForge;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.permissions.NailedPermissionFactory;
import jk_5.nailed.permissions.PermissionEventHandler;
import jk_5.nailed.players.NailedPlayerRegistry;
import jk_5.nailed.server.command.*;
import jk_5.nailed.teamspeak.TeamspeakClient;
import jk_5.nailed.util.invsee.InvSeeTicker;
import lombok.Getter;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.api.PermissionsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = NailedServer.modid, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84")
public class NailedServer {

    @Getter protected static final String modid = "Nailed";
    @SuppressWarnings("unused") @Getter private static ConfigFile config;
    @Getter private static int providerID;

    @Getter private static IrcBot ircBot;
    @Getter private static TeamspeakClient teamspeakClient;
    @Getter private static NailedPermissionFactory permissionFactory;

    public NailedServer(){
        NailedAPI.setMapLoader(new NailedMapLoader());
        NailedAPI.setMappackLoader(new NailedMappackLoader());
        NailedAPI.setPlayerRegistry(new NailedPlayerRegistry());

        if(FMLLaunchHandler.side().isClient()){
            throw new RuntimeException("Nailed-Server is server-only, don\'t use it on the client!");
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event){
        NailedLog.info("Creating config file");
        config = new ConfigFile(event.getSuggestedConfigurationFile()).setComment("Nailed main config file");

        if(NailedAPI.getMapLoader().getMapsFolder().exists()){
            NailedLog.info("Clearing away old maps folder");
            new File(".", "mapbackups").mkdirs();
            File dest = new File(new File(".", "mapbackups"), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            NailedAPI.getMapLoader().getMapsFolder().renameTo(dest);
        }

        NailedLog.info("Loading achievements");
        NailedAchievements.addAchievements();

        NailedLog.info("Initializing network pipeline");
        NailedNetworkHandler.registerChannel();

        NailedLog.info("Registering event handlers");
        MinecraftForge.EVENT_BUS.register(NailedAPI.getPlayerRegistry());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMapLoader());
        MinecraftForge.EVENT_BUS.register(NailedAPI.getMappackLoader());
        MinecraftForge.EVENT_BUS.register(new AchievementEventListener());
        MinecraftForge.EVENT_BUS.register(new StatEventHandler());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerForge());
        MinecraftForge.EVENT_BUS.register(new TeleportEventListenerEffect());
        MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
        FMLCommonHandler.instance().bus().register(NailedAPI.getPlayerRegistry());
        FMLCommonHandler.instance().bus().register(NailedAPI.getMapLoader());
        FMLCommonHandler.instance().bus().register(new InvSeeTicker());

        NailedLog.info("Registering blocks");
        NailedBlocks.init();

        NailedLog.info("Registering Nailed WorldProvider");
        NailedServer.providerID = NailedServer.config.getTag("providerId").setComment("The id for the nailed world provider").getIntValue(10);
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
        teamspeakClient = new TeamspeakClient();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.post(new RegisterInstructionEvent(InstructionReader.instance().getInstructionMap()));
        MinecraftForge.EVENT_BUS.post(new RegisterStatTypeEvent(StatTypeManager.instance().getStatTypes()));

        NailedLog.info("Registering achievements");
        NailedAchievements.init();

        NailedLog.info("Registering permissions");
        //PermissionsManager.registerPermission("nailed.commands.test1");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event){
        NailedLog.info("Loading the mappacks");
        NailedAPI.getMappackLoader().loadMappacks();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void serverStarting(FMLServerStartingEvent event){
        ircBot.connect();
        teamspeakClient.connect();

        CommandHandler ch = (CommandHandler) event.getServer().getCommandManager();

        ch.registerCommand(new CommandGoto());
        ch.registerCommand(new CommandTeam());
        ch.registerCommand(new CommandStartGame());
        ch.registerCommand(new CommandIrc());
        ch.registerCommand(new CommandMap());
        ch.registerCommand(new CommandSetWinner());
        ch.registerCommand(new CommandReloadMappacks());
        ch.registerCommand(new CommandTime());
        ch.registerCommand(new CommandSudo());
        ch.registerCommand(new CommandInvsee());
        ch.registerCommand(new CommandFirework());
        ch.registerCommand(new CommandLobby());
        ch.registerCommand(new CommandReloadMap());
        ch.registerCommand(new CommandKickall());
        ch.registerCommand(new CommandSaveMappack());
        ch.registerCommand(new CommandSafehouse());
        ch.registerCommand(new CommandTps());
        ch.registerCommand(new CommandFps());
        ch.registerCommand(new CommandCB());

        ch.getCommands().remove("tp");
        ch.getCommands().remove("toggledownfall");
        ch.getCommands().remove("gamemode");

        ch.registerCommand(new CommandTP());
        ch.registerCommand(new CommandToggleDownfall());
        ch.registerCommand(new CommandGamemode());

        ChatComponentText component = new ChatComponentText("Nailed");
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        MinecraftServer.getServer().func_147134_at().func_151315_a(component);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void serverAboutToStart(FMLServerAboutToStartEvent event){
        IpcManager.instance().start();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void serverStarted(FMLServerStartedEvent event){
        PermissionsManager.addPermissionsToFactory();

        NailedLog.info("Reading permission config");
        permissionFactory.readConfig();
    }
}
