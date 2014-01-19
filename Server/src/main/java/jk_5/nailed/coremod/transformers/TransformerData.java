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

    public static final Map<String, String> dimensionManagerObfuscated = Maps.newHashMap();
    public static final Map<String, String> dimensionManagerDeobfuscated = Maps.newHashMap();

    public static final Map<String, String> abstractClientPlayerObfuscated = Maps.newHashMap();
    public static final Map<String, String> abstractClientPlayerDeobfuscated = Maps.newHashMap();

    static {
        minecraftServerObfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerObfuscated.put("constructorSig", "(Ljava/io/File;Ljava/net/Proxy;)V");
        minecraftServerObfuscated.put("targetMethod1", "a");
        minecraftServerObfuscated.put("targetMethod1Sig", "(Ljava/lang/String;Ljava/lang/String;JLafy;Ljava/lang/String;)V");
        minecraftServerObfuscated.put("worldServerClass", "mj");
        minecraftServerObfuscated.put("worldServerConstructorSig", "(Lnet/minecraft/server/MinecraftServer;Laxo;Ljava/lang/String;ILafv;Lov;)V");
        minecraftServerObfuscated.put("saveFormatField", "j");
        minecraftServerObfuscated.put("saveFormatFieldSig", "Laxq;");
        minecraftServerObfuscated.put("saveFormatClass", "axq");
        minecraftServerObfuscated.put("getSaveLoaderName", "a");
        minecraftServerObfuscated.put("getSaveLoaderSig", "(Ljava/lang/String;Z)Laxo;");

        minecraftServerDeobfuscated.put("className", "net.minecraft.server.MinecraftServer");
        minecraftServerDeobfuscated.put("constructorSig", "(Ljava/io/File;Ljava/net/Proxy;)V");
        minecraftServerDeobfuscated.put("targetMethod1", "loadAllWorlds");
        minecraftServerDeobfuscated.put("targetMethod1Sig", "(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/WorldType;Ljava/lang/String;)V");
        minecraftServerDeobfuscated.put("worldServerClass", "net/minecraft/world/WorldServer");
        minecraftServerDeobfuscated.put("worldServerConstructorSig", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V");
        minecraftServerDeobfuscated.put("saveFormatField", "anvilConverterForAnvilFile");
        minecraftServerDeobfuscated.put("saveFormatFieldSig", "Lnet/minecraft/world/storage/ISaveFormat;");
        minecraftServerDeobfuscated.put("saveFormatClass", "net/minecraft/world/storage/ISaveFormat");
        minecraftServerDeobfuscated.put("getSaveLoaderName", "getSaveLoader");
        minecraftServerDeobfuscated.put("getSaveLoaderSig", "(Ljava/lang/String;Z)Lnet/minecraft/world/storage/ISaveHandler;");

        dimensionManagerObfuscated.put("className", "net.minecraftforge.common.DimensionManager");
        dimensionManagerObfuscated.put("targetMethodName", "initDimension");
        dimensionManagerObfuscated.put("targetMethodSig", "(I)V");
        dimensionManagerObfuscated.put("minecraftServerName", "net/minecraft/server/MinecraftServer");
        dimensionManagerObfuscated.put("getSaveFormatName", "Q");
        dimensionManagerObfuscated.put("getSaveFormatSig", "()Laxq;");
        dimensionManagerObfuscated.put("iSaveFormatName", "axq");
        dimensionManagerObfuscated.put("getSaveLoaderName", "a");
        dimensionManagerObfuscated.put("getSaveLoaderSig", "(Ljava/lang/String;Z)Laxo;");
        dimensionManagerObfuscated.put("worldServerClass", "mj");
        dimensionManagerObfuscated.put("worldServerConstructorSig", "(Lnet/minecraft/server/MinecraftServer;Laxo;Ljava/lang/String;ILafv;Lov;)V");

        dimensionManagerDeobfuscated.put("className", "net.minecraftforge.common.DimensionManager");
        dimensionManagerDeobfuscated.put("targetMethodName", "initDimension");
        dimensionManagerDeobfuscated.put("targetMethodSig", "(I)V");
        dimensionManagerDeobfuscated.put("minecraftServerName", "net/minecraft/server/MinecraftServer");
        dimensionManagerDeobfuscated.put("getSaveFormatName", "getActiveAnvilConverter");
        dimensionManagerDeobfuscated.put("getSaveFormatSig", "()Lnet/minecraft/world/storage/ISaveFormat;");
        dimensionManagerDeobfuscated.put("iSaveFormatName", "net/minecraft/world/storage/ISaveFormat");
        dimensionManagerDeobfuscated.put("getSaveLoaderName", "getSaveLoader");
        dimensionManagerDeobfuscated.put("getSaveLoaderSig", "(Ljava/lang/String;Z)Lnet/minecraft/world/storage/ISaveHandler;");
        dimensionManagerDeobfuscated.put("worldServerClass", "net/minecraft/world/WorldServer");
        dimensionManagerDeobfuscated.put("worldServerConstructorSig", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V");

        abstractClientPlayerObfuscated.put("className", "beu");
        abstractClientPlayerObfuscated.put("method1", "d");
        abstractClientPlayerObfuscated.put("method2", "e");

        abstractClientPlayerDeobfuscated.put("className", "net.minecraft.client.entity.AbstractClientPlayer");
        abstractClientPlayerDeobfuscated.put("method1", "getSkinUrl");
        abstractClientPlayerDeobfuscated.put("method2", "getCapeUrl");
    }
}
