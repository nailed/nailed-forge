package jk_5.nailed.coremod;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import jk_5.nailed.NailedLog;
import jk_5.nailed.client.ProxyClient;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.server.ProxyCommon;
import jk_5.nailed.util.config.ConfigFile;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
@NetworkMod(clientSideRequired = true)
public class NailedModContainer extends DummyModContainer {

    public static EventBus eventBus;
    public static LoadController controller;
    public static ConfigFile config;
    public static ProxyCommon proxy;

    private static Collection<Integer> registeredDimensions;

    public NailedModContainer(){
        super(new ModMetadata());
        this.getMetadata().modId = "Nailed";
        this.getMetadata().name = "Nailed";
        this.getMetadata().authorList = Arrays.asList("jk-5");
        this.getMetadata().credits = "Credits go to PostVillageCore for making the web interface";
        this.getMetadata().version = "2.0.0-SNAPSHOT";
        this.getMetadata().url = "http://github.com/nailed/nailed-forge/";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController ctr){
        bus.register(this);
        eventBus = bus;
        controller = ctr;
        return true;
    }

    @Subscribe
    public void construct(FMLConstructionEvent event){
        if(event.getSide().isClient()){
            proxy = new ProxyClient();
        }else if(event.getSide().isServer()){
            proxy = new ProxyCommon();
        }

        NailedLog.info("Registering NetworkHandler");
        try{
            FMLNetworkHandler.instance().registerNetworkMod(new NailedNetworkHandler(this));
            NailedLog.info("Successfully registered NetworkHandler");
        }catch (Exception e){
            NailedLog.severe(e, "Failed to register NetworkHandler for Nailed");
        }
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event){
        config = new ConfigFile(event.getSuggestedConfigurationFile()).setComment("Nailed main config file");
        proxy.initNetworkHandlers();
        proxy.registerEventHandlers();

        ProxyCommon.providerID = NailedModContainer.config.getTag("providerId").setComment("The id for the nailed world provider").getIntValue(10);
        DimensionManager.registerProviderType(ProxyCommon.providerID, NailedWorldProvider.class, false);
    }

    @Subscribe
    public void init(FMLInitializationEvent event){

    }

    @VersionCheckHandler
    public boolean acceptClientVersion(String version){
        return true;
    }


    private static List<Integer> getExistingMapList(){
        List<Integer> ret = Lists.newArrayList();
        File[] mapFiles = MapLoader.getMapsFolder().listFiles();
        for(File file : mapFiles){
            if(file.isDirectory() && file.getName().startsWith("map")){
                int id = Integer.parseInt(file.getName().substring(file.getName().lastIndexOf("_")));
                ret.add(id);
            }
        }
        return ret;
    }

    public static void unregisterDimensions(){
        if(registeredDimensions == null) return;
        for(Integer id : registeredDimensions){
            DimensionManager.unregisterDimension(id);
        }
    }

    public static void registerDimensions(){
        registeredDimensions = getExistingMapList();
        for(Integer id : registeredDimensions){
            if(id < -1 || id > 1) DimensionManager.registerDimension(id, ProxyCommon.providerID);
        }
    }
}
