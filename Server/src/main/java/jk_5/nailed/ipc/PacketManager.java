package jk_5.nailed.ipc;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Queues;
import jk_5.nailed.ipc.packet.*;
import lombok.Getter;

import java.util.Queue;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketManager {

    private static final BiMap<String, Class<? extends IpcPacket>> packets = HashBiMap.create();
    @Getter private static final Queue<IpcPacket> processQueue = Queues.newConcurrentLinkedQueue();

    static {
        packets.put("init", PacketInitConnection.class);
        packets.put("join", PacketPlayerJoin.class);
        packets.put("leave", PacketPlayerLeave.class);
        packets.put("kill", PacketKill.class);
        packets.put("death", PacketPlayerDeath.class);
        packets.put("auth", PacketAuthenticate.class);
        packets.put("authResponse", PacketAuthResponse.class);
    }

    public static IpcPacket getPacket(String name) throws IllegalAccessException, InstantiationException {
        if(!packets.containsKey(name)) return null;
        return packets.get(name).newInstance();
    }

    public static String getName(Class<? extends IpcPacket> cl) {
        if(!packets.containsValue(cl)) return null;
        return packets.inverse().get(cl);
    }
}
