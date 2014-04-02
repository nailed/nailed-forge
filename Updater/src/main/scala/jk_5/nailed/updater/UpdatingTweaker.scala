package jk_5.nailed.updater

import net.minecraft.launchwrapper.{LaunchClassLoader, ITweaker}
import java.util
import java.io.File
import org.apache.logging.log4j.LogManager
import jk_5.nailed.updater.json.RestartLevel
import javax.swing.JOptionPane

/**
 * No description given
 *
 * @author jk-5
 */
object UpdatingTweaker {
  var name = "NailedTest"
  var mainClass = "net.minecraft.client.main.Main"
  var gameDir = new File(".")
  var assetsDir = new File("assets")
  val logger = LogManager.getLogger("Nailed-Updater")
}
class UpdatingTweaker extends ITweaker {

  override def getLaunchArguments = new Array[String](0)
  override def getLaunchTarget = UpdatingTweaker.mainClass
  override def injectIntoClassLoader(classLoader: LaunchClassLoader){}
  override def acceptOptions(args: util.List[String], gameDir: File, assetsDir: File, profile: String){
    UpdatingTweaker.logger.info("Started nailed updater")
    UpdatingTweaker.name = profile
    UpdatingTweaker.gameDir = gameDir
    UpdatingTweaker.assetsDir = assetsDir
    if(Updater.checkForUpdates()){
      if(Updater.restart == RestartLevel.NOTHING){
        UpdatingTweaker.logger.info("Updates are done. We don\'t have to restart")
      }else if(Updater.restart == RestartLevel.GAME){
        UpdatingTweaker.logger.info("Updates are done. We have to restart the game")
        JOptionPane.showMessageDialog(null, "We updated some files and you have to restart your game. Just press \'Play\' again in the launcher", "Nailed-Updater", JOptionPane.INFORMATION_MESSAGE)
        sys.exit(0)
      }else if(Updater.restart == RestartLevel.LAUNCHER){
        UpdatingTweaker.logger.info("Updates are done. We have to restart the launcher")
        JOptionPane.showMessageDialog(null, "We updated some files and you have to restart your launcher. After the game shuts down, close your launcher and restart it and hit the \'Play\' button again", "Nailed-Updater", JOptionPane.INFORMATION_MESSAGE)
        sys.exit(0)
      }
    }
  }
}
