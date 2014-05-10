package jk_5.nailed.crashreporter

import cpw.mods.fml.common.{LoadController, ModMetadata, DummyModContainer}
import com.google.common.eventbus.{Subscribe, EventBus}
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import java.io.File
import org.apache.logging.log4j.LogManager
import cpw.mods.fml.client.{FMLFileResourcePack, FMLFolderResourcePack}

/**
 * No description given
 *
 * @author jk-5
 */
class Mod extends DummyModContainer(new ModMetadata()) {
  this.getMetadata.modId = "NailedCrashReporter"
  this.getMetadata.name = "Nailed - CrashReporter"
  this.getMetadata.version = "1.3"

  var controller: LoadController = _
  val logger = LogManager.getLogger

  override def registerBus(bus: EventBus, controller: LoadController): Boolean = {
    bus.register(this)
    this.controller = controller
    true
  }

  @Subscribe def onInit(event: FMLPreInitializationEvent){
    controller.errorOccurred(this, new RuntimeException("Bye!"))
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
      case e: Exception => logger.info("Failed to extract source from URL " + url, e)
    }
    null
  }

  override def getCustomResourcePackClass: Class[_] = {
    val source = this.getSource
    if(source == null){
      logger.warn("Failed to get source, resource pack missing")
      return null
    }
    if(source.isDirectory){
      classOf[FMLFolderResourcePack]
    }else{
      classOf[FMLFileResourcePack]
    }
  }
}
