package jk_5.nailed.client.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
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
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data){

    }

    @Override
    public String getAccessTransformerClass(){
        return "jk_5.nailed.client.coremod.transformer.AccessTransformer";
    }
}
