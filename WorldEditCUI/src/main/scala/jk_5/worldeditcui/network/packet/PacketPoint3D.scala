package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer

/**
  * No description given
  *
  * @author jk-5
  */
class PacketPoint3D extends Packet(5, 6) {

   override def process(){
     WERenderer.selection.get.setCuboidPoint(this.getInt(0), this.getInt(1), this.getInt(2), this.getInt(3))
   }
 }
