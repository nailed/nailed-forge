package jk_5.nailed.coremod.transformer;

import com.google.common.collect.Maps;
import net.minecraft.launchwrapper.Launch;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TransformerData {

    //public static final Boolean isObfuscated = !((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
    public static boolean isCommon;

    public static final Map<String, String> tcpConnectionObfuscated = Maps.newHashMap();
    public static final Map<String, String> tcpConnectionDeobfuscated = Maps.newHashMap();

    static {
        try{
            Class.forName("cpw.mods.fml.client.FMLClientHandler");
            isCommon = true;
        }catch(Exception e){
            isCommon = false;
        }

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
