package jk_5.nailed.coremod;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import jk_5.nailed.NailedLog;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@Name("Nailed|Core")
@MCVersion("1.7.2")
@TransformerExclusions({"jk_5.nailed.coremod.transformers.", "jk_5.nailed.coremod.asm."})
@SuppressWarnings("unused")
public class NailedFMLPlugin implements IFMLLoadingPlugin {

    public static boolean obfuscated = false;

    @Override
    public String[] getASMTransformerClass() {
        if(FMLLaunchHandler.side().isServer()){
            return new String[]{
                    "jk_5.nailed.coremod.transformers.ClassHeirachyTransformer",
                    "jk_5.nailed.coremod.transformers.MinecraftServerTransformer",
                    //"jk_5.nailed.coremod.transformers.S21PacketChunkDataTransformer",
                    "jk_5.nailed.coremod.transformers.VanillaSupportTransformer",
                    "jk_5.nailed.coremod.transformers.WorldServerMultiTransformer",
                    "jk_5.nailed.coremod.transformers.NetHandlerPlayServerTransformer"
            };
        }
        return new String[0];
    }

    @Override
    public String getAccessTransformerClass(){
        return "jk_5.nailed.coremod.transformers.NailedAccessTransformer";
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        obfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
        NailedLog.info("Obfuscated: {}", obfuscated);
    }
}
