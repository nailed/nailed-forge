package jk_5.nailed.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.server.ProxyCommon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedConnectionHandler implements IConnectionHandler {

    private static boolean isConnected = false;

    /**
     * Called when a player logs into the server
     *  SERVER SIDE
     */
    @Override
    public void playerLoggedIn(Player p, NetHandler netHandler, INetworkManager manager){
        EntityPlayerMP player = (EntityPlayerMP) p;
        if(player.worldObj.provider instanceof NailedWorldProvider){
            //TODO: send data?
        }
    }

    /**
     * If you don't want the connection to continue, return a non-empty string here
     * If you do, you can do other stuff here- note no FML negotiation has occured yet
     * though the client is verified as having FML installed
     *
     * SERVER SIDE
     */
    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager){
        for(Map map : MapLoader.instance().getMaps()){
            if(map.getID() < 1 || map.getID() > 1) NailedSPH.sendRegisterDimension(netHandler.getPlayer(), map.getID(), ProxyCommon.providerID);
        }
        return null;
    }

    /**
     * Fired when a remote connection is opened
     * CLIENT SIDE
     */
    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager){
        isConnected = true;
    }
    /**
     *
     * Fired when a local connection is opened
     *
     * CLIENT SIDE
     */
    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager){

    }

    /**
     * Fired when a connection closes
     *
     * ALL SIDES
     *
     * @param manager - The network manager
     */
    @Override
    public void connectionClosed(INetworkManager manager){
        if(isConnected){
            NailedModContainer.getInstance().unregisterDimensions();
            isConnected = false;
        }
    }

    /**
     * Fired when the client established the connection to the server
     *
     * CLIENT SIDE
     */
    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login){

    }
}
