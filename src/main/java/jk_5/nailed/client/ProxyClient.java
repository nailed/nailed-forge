package jk_5.nailed.client;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.server.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
public class ProxyClient extends ProxyCommon {

    public ProxyClient(){
        super();
    }

    @Override
    public void initNetworkHandlers(){
        super.initNetworkHandlers();

        NetworkRegistry.instance().registerChannel(new PacketHandlerClient(), "Nailed", Side.CLIENT);
    }

    @Override
    public void registerEventHandlers(){
        super.registerEventHandlers();

        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }
}
