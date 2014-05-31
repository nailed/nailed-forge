package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer

/**
 * No description given
 *
 * @author jk-5
 */
class PacketPoint2D extends Packet(4, 5) {

  override def process(){
    WERenderer.selection.get.setPolygonPoint(this.getInt(0), this.getInt(1), this.getInt(2))
  }
}
