package jk_5.nailed.coremod.transformers;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TransformerData {

    public static final Map<String, String> minecraftServerObfuscated = Maps.newHashMap();
    public static final Map<String, String> minecraftServerDeobfuscated = Maps.newHashMap();

    public static final Map<String, String> worldServerMultiObfuscated = Maps.newHashMap();
    public static final Map<String, String> worldServerMultiDeobfuscated = Maps.newHashMap();

    static {
        minecraftServerObfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerObfuscated.put("constructorSig", "(Ljava/io/File;Ljava/net/Proxy;)V");
        minecraftServerObfuscated.put("targetMethod1", "a");
        minecraftServerObfuscated.put("targetMethod1Sig", "(Ljava/lang/String;Ljava/lang/String;JLafy;Ljava/lang/String;)V");

        minecraftServerDeobfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerDeobfuscated.put("constructorSig", "(Ljava/io/File;Ljava/net/Proxy;)V");
        minecraftServerDeobfuscated.put("targetMethod1", "loadAllWorlds");
        minecraftServerDeobfuscated.put("targetMethod1Sig", "(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/WorldType;Ljava/lang/String;)V");

        worldServerMultiObfuscated.put("className", "mc");
        worldServerMultiObfuscated.put("constructorSig", "(Lnet/minecraft/server/MinecraftServer;Laxo;Ljava/lang/String;ILafv;Lmj;Lov;)V");

        worldServerMultiDeobfuscated.put("className", "net.minecraft.world.WorldServerMulti");
        worldServerMultiDeobfuscated.put("constructorSig", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldServer;Lnet/minecraft/profiler/Profiler;)V");
    }
}
