package jk_5.nailed.map.teamlist

import scala.collection.mutable
import scala.collection.JavaConverters._
import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.network.ByteBufUtils
import java.util

/**
 * No description given
 *
 * @author jk-5
 */

object TeamInfo {

  def read(buffer: ByteBuf) = {
    val name = ByteBufUtils.readUTF8String(buffer)
    val size = ByteBufUtils.readVarShort(buffer)
    val nList = mutable.ArrayBuffer[String]()
    val dList = mutable.ArrayBuffer[String]()
    (0 until size) foreach(i => nList += ByteBufUtils.readUTF8String(buffer)) // player (skin) names
    (0 until size) foreach(i => dList += ByteBufUtils.readUTF8String(buffer)) // display names
    new TeamInfo(name, nList, dList)
  }

  def create(name: String, pNames: util.List[String], dNames: util.List[String]) = new TeamInfo(name, pNames asScala, dNames asScala)
}

case class TeamInfo(name: String, pNames: mutable.Buffer[String] = mutable.ArrayBuffer[String](), dNames:mutable.Buffer[String] = mutable.ArrayBuffer[String]()) {

  def write(buffer: ByteBuf){
    ByteBufUtils.writeUTF8String(buffer, this.name)
    ByteBufUtils.writeVarShort(buffer, this.pNames.size)
    pNames.foreach(p => ByteBufUtils.writeUTF8String(buffer, p))
    dNames.foreach(p => ByteBufUtils.writeUTF8String(buffer, p))
  }
}
