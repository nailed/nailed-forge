package jk_5.worldeditcui.render.region

import jk_5.worldeditcui.render.shape.{Render2DGrid, Render2DBox}
import scala.collection.mutable
import jk_5.worldeditcui.render.{LineColor, PointRectangle}
import scala.collection.JavaConversions._
import jk_5.worldeditcui.render.vector.Vector2D

/**
 * No description given
 *
 * @author jk-5
 */
class PolygonRegion extends Region {

  val regionType = RegionType.POLYGON

  var points = mutable.ArrayBuffer[PointRectangle]()
  var min, max = 0

  def render(){
    if(points == null) return
    points.foreach(_.render(min, max))
    new Render2DBox(LineColor.POLYBOX, points, min, max).render()
    new Render2DGrid(LineColor.POLYGRID, points, min, max).render()
  }

  override def setMinMax(min: Int, max: Int){
    this.min = min
    this.max = max
  }

  override def setPolygonPoint(id: Int, x: Int, z: Int) {
    val point = new PointRectangle(new Vector2D(x, z))
    point.color = LineColor.POLYPOINT
    if(id < points.size){
      points.set(id, point)
    }else{
      for(i <- 0 until id - points.size){
        points += null
      }
      points.add(point)
    }
  }
}
