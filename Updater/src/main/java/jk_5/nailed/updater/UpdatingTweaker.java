package jk_5.nailed.updater;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class UpdatingTweaker implements ITweaker {

    public static final Logger logger = LogManager.getLogger("Nailed|Updater");
    public static String name = "NailedTest";

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile){
        logger.info("Started nailed updater");
        name = profile;
        Updater.checkForUpdates();
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader){

    }

    @Override
    public String getLaunchTarget(){
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments(){
        return new String[0];
    }
}
