package jk_5.worldeditcui.render.region

import jk_5.worldeditcui.render.shape.RenderEllipsoid
import jk_5.worldeditcui.render.{LineColor, PointCube}
import jk_5.worldeditcui.render.vector.Vector3D

/**
 * No description given
 *
 * @author jk-5
 */
class EllipsoidRegion extends Region {

  protected var center: PointCube = null
  protected var radii: Vector3D = null

  val regionType = RegionType.ELLIPSOID

  def render(){
    if(center != null && radii != null){
      center.render()
      new RenderEllipsoid(LineColor.ELLIPSOIDGRID, center, radii).render()
    }else if (center != null){
      center.render()
    }
  }

  override def setEllipsoidCenter(x: Int, y: Int, z: Int) {
    center = new PointCube(new Vector3D(x, y, z))
    center.color = LineColor.ELLIPSOIDCENTER
  }

  override def setEllipsoidRadii(x: Double, y: Double, z: Double) = radii = new Vector3D(x.toFloat, y.toFloat, z.toFloat)
}
