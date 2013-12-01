package jk_5.nailed.coremod.transformers;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TransformerData {

    public static final Map<String, String> tcpConnectionObfuscated = Maps.newHashMap();
    public static final Map<String, String> tcpConnectionDeobfuscated = Maps.newHashMap();

    public static final Map<String, String> minecraftServerObfuscated = Maps.newHashMap();
    public static final Map<String, String> minecraftServerDeobfuscated = Maps.newHashMap();

    static {
        tcpConnectionObfuscated.put("className", "ci");
        tcpConnectionObfuscated.put("targetMethod1", "a"); //func_74429_a
        tcpConnectionObfuscated.put("targetMethod2", "i"); //func_74447_i
        tcpConnectionObfuscated.put("packetName", "ei");

        tcpConnectionDeobfuscated.put("className", "net.minecraft.network.TcpConnection");
        tcpConnectionDeobfuscated.put("targetMethod1", "addToSendQueue");
        tcpConnectionDeobfuscated.put("targetMethod2", "readPacket");
        tcpConnectionDeobfuscated.put("packetName", "net/minecraft/network/packet/Packet");

        minecraftServerObfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerObfuscated.put("targetMethod1", "a");

        minecraftServerDeobfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerDeobfuscated.put("targetMethod1", "loadAllWorlds");
    }
}
