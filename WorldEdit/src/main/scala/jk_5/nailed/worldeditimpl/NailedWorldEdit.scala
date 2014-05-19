package jk_5.nailed.worldeditimpl

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import com.sk89q.worldedit.WorldEdit

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "NailedWorldEdit", name = "Nailed WorldEdit", version = "0.1-SNAPSHOT", modLanguage = "scala")
object NailedWorldEdit {

  @EventHandler def preInit(event: FMLPreInitializationEvent){
    WorldEdit.
  }
}
