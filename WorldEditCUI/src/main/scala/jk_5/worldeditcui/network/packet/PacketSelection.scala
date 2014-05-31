package jk_5.worldeditcui.network.packet

import jk_5.worldeditcui.render.WERenderer
import jk_5.worldeditcui.render.region._
import scala.Some

/**
  * No description given
  *
  * @author jk-5
  */
class PacketSelection extends Packet(1, 1) {
  override def process() = this.getString(0) match {
    case "cuboid" => WERenderer.selection = Some(new CuboidRegion)
    case "polygon2d" => WERenderer.selection = Some(new PolygonRegion)
    case "ellipsoid" => WERenderer.selection = Some(new EllipsoidRegion)
    case "cylinder" => WERenderer.selection = Some(new CylinderRegion)
    case _ => throw new PacketException("Invalid selection type. Must be cuboid|polygon2d|ellipsoid|cylinder")
  }
}
