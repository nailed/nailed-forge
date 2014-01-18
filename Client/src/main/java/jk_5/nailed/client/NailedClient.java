package jk_5.nailed.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import jk_5.nailed.client.achievement.NailedAchievements;
import jk_5.nailed.client.blocks.NailedBlocks;
import jk_5.nailed.client.map.NailedWorldProvider;
import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.client.render.NotificationRenderer;
import jk_5.nailed.client.render.RenderEventHandler;
import jk_5.nailed.client.util.config.ConfigFile;
import lombok.Getter;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = NailedClient.modid, version = "0.1", useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84")
public class NailedClient {

    @Getter private static final Logger logger = LogManager.getLogger("Nailed");
    @Getter protected static final String modid = "Nailed";
    @Getter private static final String minecraftVersion = "1.7.2";
    @Getter private static ConfigFile config;
    @Getter private static CreativeTabNailed creativeTab;

    @Getter @Mod.Instance(modid) private static NailedClient instance;
    @Getter private static Collection<Integer> registeredDimensions;

    @Getter private static int providerID;

    public NailedClient(){
        if(FMLLaunchHandler.side().isServer()){
            throw new RuntimeException("Nailed-Client is client-only, don\'t use it on the server!");
        }
    }

    @NetworkCheckHandler
    public boolean acceptClientVersion(String version){
        return true;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        NailedLog.info("Creating config file");
        config = new ConfigFile(event.getSuggestedConfigurationFile()).setComment("Nailed main config file");

        NailedLog.info("Loading achievements");
        NailedAchievements.addAchievements();

        NailedLog.info("Registering network handlers");
        ClientNetworkHandler.registerChannel();

        NailedLog.info("Registering event handlers");
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new SoundManager());
        MinecraftForge.EVENT_BUS.register(new NotificationRenderer());
        FMLCommonHandler.instance().bus().register(new TickHandlerClient());

        //NailedLog.info("Initializing UpdateNotifier");
        //UpdateNotificationManager.init();

        NailedLog.info("Adding creativetab");
        creativeTab = new CreativeTabNailed();

        NailedLog.info("Registering blocks");
        NailedBlocks.init();

        NailedLog.info("Registering Nailed WorldProvider");
        NailedClient.providerID = NailedClient.config.getTag("providerId").setComment("The id for the nailed world provider").getIntValue(10);
        DimensionManager.registerProviderType(NailedClient.providerID, NailedWorldProvider.class, false);

        NailedLog.info("Overriding Default WorldProviders");
        DimensionManager.unregisterProviderType(-1);
        DimensionManager.unregisterProviderType(0);
        DimensionManager.unregisterProviderType(1);
        DimensionManager.registerProviderType(-1, NailedWorldProvider.class, false);
        DimensionManager.registerProviderType(0, NailedWorldProvider.class, true);
        DimensionManager.registerProviderType(1, NailedWorldProvider.class, false);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        NailedLog.info("Registering achievements");
        NailedAchievements.init();
    }
}