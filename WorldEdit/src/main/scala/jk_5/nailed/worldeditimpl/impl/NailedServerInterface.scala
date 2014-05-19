package jk_5.nailed.worldeditimpl.impl

import com.sk89q.worldedit.{BiomeTypes, ServerInterface}
import net.minecraft.server.MinecraftServer
import scala.collection.JavaConversions._
import net.minecraft.item.Item

/**
 * No description given
 *
 * @author jk-5
 */
class NailedServerInterface(val server: MinecraftServer) extends ServerInterface {

  override def resolveItem(name: String): Int = {
    if(name == null) return 0
    val item = Option(Item.itemRegistry.getObject(name).asInstanceOf[Item])
    item match {
      case Some(i) => Item.getIdFromItem(i)
      case _ =>
        Item.itemRegistry.map(_.asInstanceOf[Item]).foreach(item => {
          var iname = item.getUnlocalizedName
          if(iname != null){
            val dotPos = iname.indexOf('.')
            if(dotPos > 0) iname = iname.substring(dotPos)
            if(name.equalsIgnoreCase(iname)) return Item.getIdFromItem(item)
          }
        })
        0
    }
  }

  override def reload(): Unit = ???

  override def getBiomes: BiomeTypes = ???

  override def isValidMobType(p1: String): Boolean = ???
}
