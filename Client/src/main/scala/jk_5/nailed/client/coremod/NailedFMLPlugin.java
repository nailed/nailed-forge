package jk_5.nailed.client.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import jk_5.nailed.client.Constants;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@IFMLLoadingPlugin.MCVersion(Constants.MCVERSION)
public class NailedFMLPlugin implements IFMLLoadingPlugin {

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
        return "jk_5.nailed.client.coremod.NailedSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> data){

    }

    @Override
    public String getAccessTransformerClass(){
        return "jk_5.nailed.client.coremod.transformer.AccessTransformer";
    }
}
