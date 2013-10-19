package jk_5.nailed.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@MCVersion("1.6.4")
@Name("Nailed")
@TransformerExclusions({"jk_5.nailed.coremod.", "jk_5.nailed.coremod.NailedValidator"})
@SuppressWarnings("unused")
public class NailedPlugin implements IFMLLoadingPlugin {

    public String[] getLibraryRequestClass(){
        return new String[]{};
    }

    public String[] getASMTransformerClass(){
        return new String[]{
                "jk_5.nailed.coremod.transformer.PacketTransformer"
        };
    }

    public String getModContainerClass(){
        return "jk_5.nailed.coremod.NailedModContainer";
    }

    public String getSetupClass(){
        return "jk_5.nailed.coremod.NailedValidator";
    }

    public void injectData(Map<String, Object> data){

    }
}
