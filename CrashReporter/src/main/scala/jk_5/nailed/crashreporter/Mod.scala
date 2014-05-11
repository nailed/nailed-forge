package jk_5.nailed.crashreporter

import cpw.mods.fml.common.{LoadController, ModMetadata, DummyModContainer}
import com.google.common.eventbus.{Subscribe, EventBus}
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import java.io._
import org.apache.logging.log4j.LogManager
import cpw.mods.fml.client.{FMLFileResourcePack, FMLFolderResourcePack}
import java.util
import org.apache.commons.io.IOUtils
import com.google.gson.{JsonObject, JsonParser}

/**
 * No description given
 *
 * @author jk-5
 */
object Mod {
  val logger = LogManager.getLogger
  lazy val config: JsonObject = {
    val configDir = new File("config")
    if(!configDir.exists()) configDir.mkdirs()
    val configFile = new File(configDir, "crashreporter.json")
    if(!configFile.exists){
      logger.info("Loading default config file")
      var is: InputStream = null
      var pw: PrintWriter = null
      try{
        is = classOf[Mod].getResourceAsStream("/assets/nailedcrashreporter/config.json")
        pw = new PrintWriter(configFile)
        IOUtils.copy(is, pw)
      }catch{
        case e: Exception =>
          logger.fatal("Error while creating default config file", e)
      }finally{
        IOUtils.closeQuietly(is)
        IOUtils.closeQuietly(pw)
      }
    }
    logger.info("Creating config file")
    var fr: FileReader = null
    try{
      fr = new FileReader(configFile)
      new JsonParser().parse(fr).asInstanceOf[JsonObject]
    }catch{
      case e: FileNotFoundException => e.printStackTrace(); null
    }finally{
      IOUtils.closeQuietly(fr)
    }
  }
}

class Mod extends DummyModContainer(new ModMetadata()) {
  this.getMetadata.modId = "NailedCrashReporter"
  this.getMetadata.name = "Nailed - CrashReporter"
  this.getMetadata.version = "1.3"
  this.getMetadata.authorList = util.Arrays.asList("jk-5")

  var controller: LoadController = _

  override def registerBus(bus: EventBus, controller: LoadController): Boolean = {
    bus.register(this)
    this.controller = controller
    true
  }

  @Subscribe def onPreinit(event: FMLPreInitializationEvent){
    //controller.errorOccurred(this, new RuntimeException("Bye!"))
  }

  override def getSource: File = {
    val injected = CorePlugin.injectedLocation
    if(injected != null) return injected
    val url = this.getClass.getResource(".")
    try{
      var root = new File(url.toURI)
      if(root.getName.equals("jk_5")){
        root = root.getParentFile
      }
      return root
    }catch{
      case e: Exception => Mod.logger.info("Failed to extract source from URL " + url, e)
    }
    null
  }

  override def getCustomResourcePackClass: Class[_] = {
    val source = this.getSource
    if(source == null){
      Mod.logger.warn("Failed to get source, resource pack missing")
      return null
    }
    if(source.isDirectory){
      classOf[FMLFolderResourcePack]
    }else{
      classOf[FMLFileResourcePack]
    }
  }
}
