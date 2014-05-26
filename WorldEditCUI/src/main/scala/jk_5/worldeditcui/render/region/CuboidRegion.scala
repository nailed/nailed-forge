package jk_5.worldeditcui.render.region

import jk_5.worldeditcui.render.{LineColor, PointCube}
import jk_5.worldeditcui.render.vector.Vector3D
import jk_5.worldeditcui.render.shape.{Render3DGrid, Render3DBox}

/**
 * No description given
 *
 * @author jk-5
 */
class CuboidRegion extends Region {

  protected var firstPoint: PointCube = null
  protected var secondPoint: PointCube = null

  val regionType = RegionType.CUBOID

  def render(){
    if(firstPoint != null && secondPoint != null){
      firstPoint.render()
      secondPoint.render()
      val bounds = this.calcBounds
      new Render3DBox(LineColor.CUBOIDBOX, bounds(0), bounds(1)).render()
      new Render3DGrid(LineColor.CUBOIDGRID, bounds(0), bounds(1)).render()
    }else if (firstPoint != null){
      firstPoint.render()
    }else if (secondPoint != null){
      secondPoint.render()
    }
  }

  override def setCuboidPoint(id: Int, x: Int, y: Int, z: Int) {
    if(id == 0){
      firstPoint = new PointCube(new Vector3D(x, y, z))
      firstPoint.color = LineColor.CUBOIDPOINT1
    }else if (id == 1){
      secondPoint = new PointCube(new Vector3D(x, y, z))
      secondPoint.color = LineColor.CUBOIDPOINT2
    }
  }

  protected def calcBounds: Array[Vector3D] = {
    val off = 0.02f
    val off1 = 1 + off
    var x0, y0, z0, x1, y1, z1 = 0F
    for(point <- Array(firstPoint, secondPoint)){
      if(point.point.getX + off1 > x1){
        x1 = point.point.getX + off1
      }
      if(point.point.getX - off < x0){
        x0 = point.point.getX - off
      }
      if(point.point.getY + off1 > y1){
        y1 = point.point.getY + off1
      }
      if(point.point.getY - off < y0){
        y0 = point.point.getY - off
      }
      if(point.point.getZ + off1 > z1){
        z1 = point.point.getZ + off1
      }
      if(point.point.getZ - off < z0){
        z0 = point.point.getZ - off
      }
    }
    Array(new Vector3D(x0, y0, z0), new Vector3D(x1, y1, z1))
  }
}
