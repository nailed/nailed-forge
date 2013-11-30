package jk_5.nailed;

import codechicken.lib.packet.PacketCustom.CustomTinyPacketHandler;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import jk_5.nailed.achievement.NailedAchievements;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.map.instruction.InstructionReader;
import jk_5.nailed.map.instruction.RegisterInstructionEvent;
import jk_5.nailed.server.ProxyCommon;
import jk_5.nailed.server.command.CommandGoto;
import jk_5.nailed.util.config.ConfigFile;
import lombok.Getter;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = NailedModContainer.modid, useMetadata = true, certificateFingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, tinyPacketHandler = CustomTinyPacketHandler.class)
public class NailedModContainer {

    @Getter protected static final String modid = "Nailed";

    @Getter
    private static ConfigFile config;

    @SidedProxy(modId = modid, clientSide = "jk_5.nailed.client.ProxyClient", serverSide = "jk_5.nailed.server.ProxyCommon")
    public static ProxyCommon proxy;

    @Getter
    @Instance(modid)
    private static NailedModContainer instance;

    @Getter private static Collection<Integer> registeredDimensions;

    public NailedModContainer(){
        NailedAPI.setMappackRegistrar(MapLoader.instance());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        NailedLog.info("Creating config file");
        config = new ConfigFile(event.getSuggestedConfigurationFile()).setComment("Nailed main config file");

        NailedLog.info("Loading achievements");
        NailedAchievements.addAchievements();

        NailedLog.info("Initializing network and event handlers");
        proxy.initNetworkHandlers();
        proxy.registerEventHandlers();

        NailedLog.info("Registering blocks");
        NailedBlocks.init();

        NailedLog.info("Registering Nailed WorldProvider");
        ProxyCommon.providerID = NailedModContainer.config.getTag("providerId").setComment("The id for the nailed world provider").getIntValue(10);
        DimensionManager.registerProviderType(ProxyCommon.providerID, NailedWorldProvider.class, false);

        NailedLog.info("Overriding Default WorldProviders");
        DimensionManager.unregisterProviderType(-1);
        DimensionManager.unregisterProviderType(0);
        DimensionManager.unregisterProviderType(1);
        DimensionManager.registerProviderType(-1, NailedWorldProvider.class, false);
        DimensionManager.registerProviderType(0, NailedWorldProvider.class, true);
        DimensionManager.registerProviderType(1, NailedWorldProvider.class, false);

        MinecraftForge.EVENT_BUS.post(new RegisterInstructionEvent(InstructionReader.instance().getInstructionMap()));
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        NailedLog.info("Initializing Nailed");
        MapLoader.instance().loadMappacks();

        NailedLog.info("Registering achievements");
        NailedAchievements.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandGoto());
    }

    @VersionCheckHandler
    public boolean acceptClientVersion(String version){
        return true;
    }

    private List<Integer> getExistingMapList(){
        List<Integer> ret = Lists.newArrayList();
        File[] mapFiles = MapLoader.getMapsFolder().listFiles();
        if(mapFiles == null) return ret;
        for(File file : mapFiles){
            if(file.isDirectory() && file.getName().startsWith("map")){
                int id = Integer.parseInt(file.getName().substring(file.getName().lastIndexOf("_")));
                ret.add(id);
            }
        }
        return ret;
    }

    public void unregisterDimensions(){
        if(registeredDimensions == null) return;
        for(Integer id : registeredDimensions){
            DimensionManager.unregisterDimension(id);
        }
    }

    public void registerDimensions(){
        registeredDimensions = getExistingMapList();
        for(Integer id : registeredDimensions){
            if(id < -1 || id > 1) DimensionManager.registerDimension(id, ProxyCommon.providerID);
        }
    }
}
