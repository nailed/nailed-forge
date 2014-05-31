package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer

/**
 * No description given
 *
 * @author jk-5
 */
class PacketMinMax extends Packet(2, 2) {

  override def process(){
    WERenderer.selection.get.setMinMax(this.getInt(0), this.getInt(1))
  }
}
