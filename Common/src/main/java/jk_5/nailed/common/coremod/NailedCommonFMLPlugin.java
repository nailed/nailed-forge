package jk_5.nailed.common.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@IFMLLoadingPlugin.MCVersion
public class NailedCommonFMLPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass(){
        return new String[0];
    }

    @Override
    public String getModContainerClass(){
        return null;
    }

    @Override
    public String getSetupClass(){
        return "jk_5.nailed.common.coremod.NailedSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> stringObjectMap){

    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
