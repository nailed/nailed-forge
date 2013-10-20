package jk_5.nailed.coremod.transformer;

import com.google.common.collect.Maps;
import jk_5.nailed.coremod.NailedPlugin;
import net.minecraft.launchwrapper.Launch;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TransformerData {

    public static final Map<String, String> tcpConnectionObfuscated = Maps.newHashMap();
    public static final Map<String, String> tcpConnectionDeobfuscated = Maps.newHashMap();

    static {
        tcpConnectionObfuscated.put("className", "ci");
        tcpConnectionObfuscated.put("targetMethod1", "a"); //func_74429_a
        tcpConnectionObfuscated.put("targetMethod2", "i"); //func_74447_i
        tcpConnectionObfuscated.put("packetName", "ei");

        tcpConnectionDeobfuscated.put("className", "net.minecraft.network.TcpConnection");
        tcpConnectionDeobfuscated.put("targetMethod1", "addToSendQueue");
        tcpConnectionDeobfuscated.put("targetMethod2", "readPacket");
        tcpConnectionDeobfuscated.put("packetName", "net/minecraft/network/packet/Packet");
    }
}
