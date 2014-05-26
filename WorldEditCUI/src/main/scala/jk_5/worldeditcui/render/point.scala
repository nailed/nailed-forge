package jk_5.worldeditcui.render

import jk_5.worldeditcui.render.vector.{Vector2D, Vector3D}
import jk_5.worldeditcui.render.shape.Render3DBox

/**
 * No description given
 *
 * @author jk-5
 */
class PointCube(val point: Vector3D) {
  var color = LineColor.CUBOIDPOINT1

  def render(){
    val off = 0.03f
    val minVec = new Vector3D(off, off, off)
    val maxVec = new Vector3D(off + 1, off + 1, off + 1)
    new Render3DBox(color, point.subtract(minVec), point.add(maxVec)).render()
  }
}

class PointRectangle(val point: Vector2D) {
  var color = LineColor.POLYPOINT

  def render(min: Int, max: Int){
    val off = 0.03f
    val minVec = new Vector2D(off, off)
    val maxVec = new Vector2D(off + 1, off + 1)
    new Render3DBox(color, point.subtract(minVec).to3D(min - off), point.add(maxVec).to3D(max + 1 + off)).render()
  }
}
