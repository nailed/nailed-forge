package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer

/**
 * No description given
 *
 * @author jk-5
 */
class PacketEllipsoid extends Packet(4, 4) {

  override def process(){
    val id = this.getInt(0)
    if(id == 0){
      val x = this.getInt(1)
      val y = this.getInt(2)
      val z = this.getInt(3)
      WERenderer.selection.get.setEllipsoidCenter(x, y, z)
    }else if(id == 1){
      val x = this.getDouble(1)
      val y = this.getDouble(2)
      val z = this.getDouble(3)
      WERenderer.selection.get.setEllipsoidRadii(x, y, z)
    }
  }
}
