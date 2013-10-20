package jk_5.nailed.server;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import jk_5.nailed.network.packets.NailedPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketHandlerServer implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player){
        NailedPacket.readPacket(packet).processPacket(manager, (EntityPlayer) player);
    }
}
