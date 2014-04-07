package jk_5.nailed.map.script;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.scripting.IMount;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class MountUtils {

    public static IMount createResourceMount(Class modClass, String domain, String subPath){
        try{
            File jar = getLoadingJar(modClass);
            if(jar != null){
                List<IMount> mounts = Lists.newArrayList();
                subPath = "assets/" + domain + "/" + subPath;

                IMount mount;
                if(jar.getName().endsWith(".class")){
                    //Somehow we are not in a jar. Development environment?
                    mounts.add(mount = new ReadOnlyMount(new File(jar.getParentFile().getParentFile().getParentFile(), subPath)));
                }else{
                    mounts.add(mount = new JarMount(jar, subPath));
                }

                File resourcePackDir = getResourcePackDir();
                if((resourcePackDir.exists()) && (resourcePackDir.isDirectory())){
                    String[] resourcePacks = resourcePackDir.list();
                    for(String pack : resourcePacks){
                        try{
                            File resourcePack = new File(resourcePackDir, pack);
                            if(!resourcePack.isDirectory()){
                                IMount resourcePackMount = new JarMount(resourcePack, subPath);
                                mounts.add(resourcePackMount);
                            }else{
                                File subResource = new File(resourcePack, subPath);
                                if(subResource.exists()){
                                    IMount resourcePackMount = new FileMount(subResource, 0L);
                                    mounts.add(resourcePackMount);
                                }
                            }
                        }catch(IOException e){
                        }
                    }
                }

                if(mounts.size() > 1){
                    IMount[] mountArray = new IMount[mounts.size()];
                    mounts.toArray(mountArray);
                    return new ComboMount(mountArray);
                }
                return mount;
            }

            return null;
        }catch(IOException e){
            NailedLog.error("Error while mounting assets/{}/{}", domain, subPath);
            NailedLog.error("Error: ", e);
        }
        return null;
    }

    private static File getLoadingJar(Class modClass){
        String path = modClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        int bangIndex = path.indexOf("!");
        if(bangIndex >= 0){
            path = path.substring(0, bangIndex);
        }
        URL url;
        try{
            url = new URL(path);
        }catch(MalformedURLException e1){
            return new File(path);
        }
        File file;
        try{
            file = new File(url.toURI());
        }catch(URISyntaxException e){
            file = new File(url.getPath());
        }
        return file;
    }

    private static File getBaseDir(){
        return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(".");
    }

    private static File getResourcePackDir(){
        return new File(getBaseDir(), "resourcepacks");
    }
}
