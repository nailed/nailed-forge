package jk_5.nailed.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import jk_5.nailed.coremod.NailedModContainer;
import jk_5.nailed.map.gen.NailedWorldProvider;
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
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager){
        /*for(Map map : MapLoader.instance().getMaps()){
            if(map.getID() < 1 || map.getID() > 1) manager.addToSendQueue(new PacketRegisterDimension(map.getID()).getPacket());
        }*/
        return null;
    }

    /**
     * Fired when a remote connection is opened
     * CLIENT SIDE
     */
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager){
        isConnected = true;
    }
    /**
     *
     * Fired when a local connection is opened
     *
     * CLIENT SIDE
     */
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager){

    }

    /**
     * Fired when a connection closes
     *
     * ALL SIDES
     *
     * @param manager
     */
    public void connectionClosed(INetworkManager manager){
        if(isConnected){
            NailedModContainer.unregisterDimensions();
            isConnected = false;
        }
    }

    /**
     * Fired when the client established the connection to the server
     *
     * CLIENT SIDE
     */
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login){

    }
}
