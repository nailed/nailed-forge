package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit.{UnknownBiomeTypeException, BiomeType, BiomeTypes}
import scala.collection.mutable
import net.minecraft.world.biome.BiomeGenBase
import java.util

/**
 * No description given
 *
 * @author jk-5
 */
object NailedBiomeTypes extends BiomeTypes {

  val biomes = mutable.ArrayBuffer[NailedBiomeType]()
  val biomeTypeMap = mutable.HashMap[BiomeGenBase, NailedBiomeType]()

  BiomeGenBase.getBiomeGenArray.filter(_ != null).foreach(biome => {
    val typ = new NailedBiomeType(biome)
    this.biomes += typ
    this.biomeTypeMap.put(biome, typ)
  })

  def getFromBaseBiome(biome: BiomeGenBase) = this.biomeTypeMap.get(biome).getOrElse(BiomeType.UNKNOWN)
  def getFromBiomeType(typ: NailedBiomeType) = if(typ != null) typ.biome else null

  override def has(name: String): Boolean = {
    if(name == null) return false
    this.biomes.exists(_.nameMatches(name))
  }

  override def get(name: String): BiomeType = {
    val typ = this.biomes.find(_.nameMatches(name))
    if(typ.isEmpty){
      throw new UnknownBiomeTypeException(name)
    }else{
      typ.get
    }
  }

  override def all(): util.List[BiomeType] = {
    val list = new util.ArrayList[BiomeType]()
    this.biomes.foreach(list.add(_))
    list
  }
}
