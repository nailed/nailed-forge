package jk_5.nailed.network;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet209SetPlayerTeam;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketListener {

    private static ArrayListMultimap<Integer, IPacketListener> listeners = ArrayListMultimap.create();

    public static void register(IPacketListener listener){
        for (int i = 0; i < 256; i ++){
            listeners.put(i, listener);
        }
    }

    public static void register(IPacketListener listener, int[] ids){
        for (int i : ids){
            listeners.put(i, listener);
        }
    }

    public static Packet handleIncoming(Packet packet){
        if(packet == null) return null;
        if(packet instanceof Packet209SetPlayerTeam){
            System.out.println(((Packet209SetPlayerTeam) packet).mode);
        }
        for(IPacketListener listener : listeners.get(packet.getPacketId())){
            packet = listener.handleIncoming(packet);
            if(packet == null) return null;
        }
        return packet;
    }

    public static Packet handleOutgoing(Packet packet){
        if(packet == null) return null;
        if(packet instanceof Packet209SetPlayerTeam){
            System.out.println(((Packet209SetPlayerTeam) packet).mode);
        }
        for(IPacketListener listener : listeners.get(packet.getPacketId())){
            packet = listener.handleOutgoing(packet);
            if(packet == null) return null;
        }
        return packet;
    }
}
