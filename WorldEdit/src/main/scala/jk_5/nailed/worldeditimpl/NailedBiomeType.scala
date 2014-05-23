package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit.BiomeType
import net.minecraft.world.biome.BiomeGenBase

/**
 * No description given
 *
 * @author jk-5
 */
class NailedBiomeType(val biome: BiomeGenBase) extends BiomeType {

  val name = biome.biomeName.toLowerCase
  val biomeNames = Array(this.name, this.name.replace(' ', '_'), this.name.replace(" ", ""))
  override def getName = this.biome.biomeName
  def nameMatches(name: String) = this.biomeNames.contains(name.toLowerCase)
}
