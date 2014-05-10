package jk_5.nailed.crashreporter

import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions
import java.util
import java.io.File

/**
 * No description given
 *
 * @author jk-5
 */
object CorePlugin {
  var injectedLocation: File = _
}
@TransformerExclusions(Array("jk_5.nailed.crashreporter.asm.", "scala."))
class CorePlugin extends IFMLLoadingPlugin {
  override def getASMTransformerClass = Array("jk_5.nailed.crashreporter.asm.Transformer")
  override def injectData(data: util.Map[String, AnyRef]){
    CorePlugin.injectedLocation = data.get("coremodLocation").asInstanceOf[File]
    CrashReportDataCollector.main(new Array[String](0))
  }
  override def getModContainerClass = "jk_5.nailed.crashreporter.Mod"
  override def getAccessTransformerClass: String = null
  override def getSetupClass: String = null
}
