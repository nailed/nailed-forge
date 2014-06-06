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
    val names = mutable.ArrayBuffer[String]()
    (0 until size) foreach(i => names += ByteBufUtils.readUTF8String(buffer))
    new TeamInfo(name, names)
  }

  def create(name: String, names: util.List[String]) = new TeamInfo(name, names asScala)
}

case class TeamInfo(name: String, usernames: mutable.Buffer[String] = mutable.ArrayBuffer[String]()) {

  def write(buffer: ByteBuf){
    ByteBufUtils.writeUTF8String(buffer, this.name)
    ByteBufUtils.writeVarShort(buffer, this.usernames.size)
    usernames.foreach(p => ByteBufUtils.writeUTF8String(buffer, p))
  }
}
