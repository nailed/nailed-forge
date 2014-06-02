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
    val list = mutable.ArrayBuffer[String]()
    (0 until size) foreach(i => list += ByteBufUtils.readUTF8String(buffer))
    new TeamInfo(name, list)
  }

  def create(name: String, players: util.List[String]) = new TeamInfo(name, players asScala)
}

case class TeamInfo(name: String, players: mutable.Buffer[String] = mutable.ArrayBuffer[String]()) {

  def write(buffer: ByteBuf){
    ByteBufUtils.writeUTF8String(buffer, this.name)
    ByteBufUtils.writeVarShort(buffer, this.players.size)
    players.foreach(p => ByteBufUtils.writeUTF8String(buffer, p))
  }
}
