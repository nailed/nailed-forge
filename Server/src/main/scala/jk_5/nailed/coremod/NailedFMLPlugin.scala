package jk_5.nailed.coremod

import cpw.mods.fml.relauncher.IFMLLoadingPlugin._
import cpw.mods.fml.relauncher.IFMLLoadingPlugin
import java.util
import jk_5.nailed.NailedLog

/**
 * No description given
 *
 * @author jk-5
 */
object NailedFMLPlugin {
  var obfuscated = false
}

@Name("Nailed|Core")
@MCVersion("1.7.2")
@TransformerExclusions(Array("jk_5.nailed.coremod.transformers.", "jk_5.nailed.coremod.asm.", "scala."))
@SuppressWarnings("unused")
class NailedFMLPlugin extends IFMLLoadingPlugin{

  override def getASMTransformerClass = Array(
    "jk_5.nailed.coremod.transformers.ClassHeirachyTransformer",
    "jk_5.nailed.coremod.transformers.MinecraftServerTransformer",
    //"jk_5.nailed.coremod.transformers.S21PacketChunkDataTransformer",
    "jk_5.nailed.coremod.transformers.VanillaSupportTransformer",
    "jk_5.nailed.coremod.transformers.WorldServerMultiTransformer",
    "jk_5.nailed.coremod.transformers.DimensionManagerTransformer",
    "jk_5.nailed.coremod.transformers.NetworkSystemTransformer",
    "jk_5.nailed.coremod.transformers.NetHandlerPlayServerTransformer"
  )

  override def getAccessTransformerClass = "jk_5.nailed.coremod.transformers.NailedAccessTransformer"
  override def getModContainerClass: String = null
  override def getSetupClass: String = null
  override def injectData(data: util.Map[String, AnyRef]){
    NailedFMLPlugin.obfuscated = data.get("runtimeDeobfuscationEnabled").asInstanceOf[java.lang.Boolean]
    NailedLog.info("Obfuscated: {}", NailedFMLPlugin.obfuscated)
  }
}
