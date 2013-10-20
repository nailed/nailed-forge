package jk_5.nailed.coremod;

import cpw.mods.fml.relauncher.IFMLCallHook;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.security.CodeSource;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedValidator implements IFMLCallHook {

    @Override
    public void injectData(Map<String,Object> data){
        if(data.containsKey("classLoader")){
            NailedPlugin.classLoader = (LaunchClassLoader) data.get("classLoader");
        }
    }

    @Override
    public Void call() throws Exception{
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if(codeSource.getLocation().getProtocol().equals("jar")){
            //We're a jar. Check the signature!
        }
        return null;
    }
}
