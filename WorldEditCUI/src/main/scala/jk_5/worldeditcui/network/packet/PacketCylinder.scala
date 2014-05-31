package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer

/**
 * No description given
 *
 * @author jk-5
 */
class PacketCylinder extends Packet(5, 5) {

  override def process(){
    WERenderer.selection.get.setCylinderCenter(this.getInt(0), this.getInt(1), this.getInt(2))
    WERenderer.selection.get.setCylinderRadius(this.getDouble(3), this.getDouble(4))
  }
}
