package jk_5.worldeditcui.render.region

import jk_5.worldeditcui.render.shape.{RenderCylinderBox, RenderCylinderGrid, RenderCylinderCircles}
import jk_5.worldeditcui.render.{LineColor, PointCube}
import jk_5.worldeditcui.render.vector.Vector3D

/**
 * No description given
 *
 * @author jk-5
 */
class CylinderRegion extends Region {

  val regionType = RegionType.CYLINDER

  protected var center: PointCube = null
  protected var radX = 0D
  protected var radZ = 0D
  protected var minY = 0
  protected var maxY = 0

  def render(){
    if(center != null){
      center.render()
      var tMin = minY
      var tMax = maxY
      if(minY == 0 || maxY == 0){
        tMin = center.point.getY.toInt
        tMax = center.point.getY.toInt
      }
      new RenderCylinderCircles(LineColor.CYLINDERGRID, center, radX, radZ, tMin, tMax).render()
      new RenderCylinderGrid(LineColor.CYLINDERGRID, center, radX, radZ, tMin, tMax).render()
      new RenderCylinderBox(LineColor.CYLINDERBOX, center, radX, radZ, tMin, tMax).render()
    }
  }

  override def setCylinderCenter(x: Int, y: Int, z: Int){
    center = new PointCube(new Vector3D(x, y, z))
    center.color = LineColor.CYLINDERCENTER
  }

  override def setCylinderRadius(x: Double, z: Double){
    this.radX = x
    this.radZ = z
  }

  override def setMinMax(min: Int, max: Int) {
    minY = min
    maxY = max
  }
}
