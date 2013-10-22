package jk_5.nailed.server;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.coremod.NailedModContainer;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.network.NailedConnectionHandler;
import jk_5.nailed.network.NailedPlayerTracker;
import jk_5.nailed.server.command.CommandGoto;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
public class ProxyCommon {

    public static int providerID;

    public ProxyCommon(){
        NailedModContainer.eventBus.register(this);
    }

    @Subscribe
    public void serverPreInit(FMLPreInitializationEvent event){
        MapLoader.instance().loadMappacks();
    }

    @Subscribe
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandGoto());
    }

    public void initNetworkHandlers(){
        NetworkRegistry.instance().registerChannel(new PacketHandlerServer(), "Nailed", Side.SERVER);
        NetworkRegistry.instance().registerConnectionHandler(new NailedConnectionHandler());
        GameRegistry.registerPlayerTracker(new NailedPlayerTracker());
    }

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
    }
}
