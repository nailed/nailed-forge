package jk_5.nailed.network;

import net.minecraft.network.packet.Packet;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IPacketListener {

    public Packet handleIncoming(Packet packet);
    public Packet handleOutgoing(Packet packet);
}
