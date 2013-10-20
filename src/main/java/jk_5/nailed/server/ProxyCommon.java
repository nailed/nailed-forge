package jk_5.nailed.server;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.coremod.NailedModContainer;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.server.command.CommandGoto;
import lombok.Getter;
import net.minecraftforge.common.DimensionManager;
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
    }

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
    }
}
