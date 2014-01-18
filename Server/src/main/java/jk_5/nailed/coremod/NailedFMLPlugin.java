package jk_5.nailed.coremod;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@Name("Nailed|Core")
@MCVersion("1.7.2")
@TransformerExclusions({"jk_5.nailed.coremod.transformers."})
@SuppressWarnings("unused")
public class NailedFMLPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        if(FMLLaunchHandler.side().isClient()){
            return new String[]{
                    //"jk_5.nailed.coremod.transformers.AbstractClientPlayerTransformer"
            };
        }else{
            return new String[]{
                    "jk_5.nailed.coremod.transformers.MinecraftServerTransformer",
                    "jk_5.nailed.coremod.transformers.DimensionManagerTransformer"
            };
        }
    }

    @Override
    public String getAccessTransformerClass(){
        return null;
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

    }
}