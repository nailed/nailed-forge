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

    public static String name = "NailedTest";
    public static File gameDir = new File(".");
    public static File assetsDir = new File("assets");
    public static String mainClass;
    public static final Logger logger = LogManager.getLogger("Nailed-Updater");

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile){
        logger.info("Starting nailed updater...");
        UpdatingTweaker.name = profile;
        UpdatingTweaker.gameDir = gameDir;
        UpdatingTweaker.assetsDir = assetsDir;
        Updater2.run();
        /*if(Updater.checkForUpdates()){
            if(Updater.getRestartLevel() == 0){
                System.out.println("Updates are done. We don\'t have to restart");
            }else if(Updater.getRestartLevel() == 1){
                System.out.println("Updates are done. We have to restart the game");
                JOptionPane.showMessageDialog(null, "We updated some files and you have to restart your game. Just press \'Play\' again in the launcher", "Nailed-Updater", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }else if(Updater.getRestartLevel() == 2){
                System.out.println("Updates are done. We have to restart the launcher");
                JOptionPane.showMessageDialog(null, "We updated some files and you have to restart your launcher. After the game shuts down, close your launcher and restart it and hit the \'Play\' button again", "Nailed-Updater", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        }*/
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader){

    }

    @Override
    public String getLaunchTarget(){
        return mainClass;
    }

    @Override
    public String[] getLaunchArguments(){
        return new String[0];
    }
}
