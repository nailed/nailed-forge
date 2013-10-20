package jk_5.nailed.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@MCVersion("1.6.4")
@Name("Nailed")
@TransformerExclusions({"jk_5.nailed.coremod.transformer.", "jk_5.nailed.coremod.NailedValidator"})
@SuppressWarnings("unused")
public class NailedPlugin implements IFMLLoadingPlugin {

    public static LaunchClassLoader classLoader;

    @Override
    public String[] getLibraryRequestClass(){
        return new String[]{};
    }

    @Override
    public String[] getASMTransformerClass(){
        return new String[]{
                "jk_5.nailed.coremod.transformer.PacketTransformer",
                "jk_5.nailed.coremod.transformer.NailedAccessTransformer"
        };
    }

    @Override
    public String getModContainerClass(){
        return "jk_5.nailed.coremod.NailedModContainer";
    }

    @Override
    public String getSetupClass(){
        return "jk_5.nailed.coremod.NailedValidator";
    }

    @Override
    public void injectData(Map<String, Object> data){

    }
}
