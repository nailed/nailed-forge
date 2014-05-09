package jk_5.nailed

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.network.NetworkCheckHandler
import cpw.mods.fml.relauncher.Side

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "nailedCommon", name = "Nailed Common", modLanguage = "scala", version = "0.1")
object NailedCommon {
  @NetworkCheckHandler def check(versions: java.util.Map[String, String], side: Side) = true
}
