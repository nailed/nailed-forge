package jk_5.worldeditcui.render.shape

import jk_5.worldeditcui.render.{PointCube, LineColor}
import net.minecraft.client.renderer.Tessellator.{instance => tess}
import org.lwjgl.opengl.GL11
import jk_5.worldeditcui.WorldEditCUI

/**
 * No description given
 *
 * @author jk-5
 */
class RenderCylinderGrid(val color: LineColor, val center: PointCube, val radX: Double, val radZ: Double, val minY: Int, val maxY: Int) {
  val centerX = center.point.getX + 0.5
  val centerZ = center.point.getZ + 0.5

  def render(){
    color.getColors.foreach(color => {
      color.prepareRender()
      val tmaxY = maxY + 1
      val tminY = minY
      val posRadiusX = Math.ceil(radX).toInt
      val negRadiusX = -Math.ceil(radX).toInt
      val posRadiusZ = Math.ceil(radZ).toInt
      val negRadiusZ = -Math.ceil(radZ).toInt

      for(x <- negRadiusX to posRadiusX){
        val z = radZ * Math.cos(Math.asin(x / radX))
        tess.startDrawing(GL11.GL_LINE_LOOP)
        color.prepareColor()
        tess.addVertex(centerX + x, tmaxY, centerZ + z)
        tess.addVertex(centerX + x, tmaxY, centerZ - z)
        tess.addVertex(centerX + x, tminY, centerZ - z)
        tess.addVertex(centerX + x, tminY, centerZ + z)
        tess.draw
      }
      for(z <- negRadiusZ to posRadiusZ){
        val x = radX * Math.sin(Math.acos(z / radZ))
        tess.startDrawing(GL11.GL_LINE_LOOP)
        color.prepareColor()
        tess.addVertex(centerX + x, tmaxY, centerZ + z)
        tess.addVertex(centerX - x, tmaxY, centerZ + z)
        tess.addVertex(centerX - x, tminY, centerZ + z)
        tess.addVertex(centerX + x, tminY, centerZ + z)
        tess.draw
      }
    })
  }
}
