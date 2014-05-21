package jk_5.nailed.worldeditimpl

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkCheckHandler
import cpw.mods.fml.relauncher.Side

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "NailedWorldEdit", name = "Nailed WorldEdit", version = "0.1-SNAPSHOT", modLanguage = "scala")
object NailedWorldEdit {

  @NetworkCheckHandler def accept(versions: java.util.Map[String, String], side: Side) = true

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    //WorldEdit.
  }
}
