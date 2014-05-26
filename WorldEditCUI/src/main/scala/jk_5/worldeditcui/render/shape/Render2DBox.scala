package jk_5.worldeditcui.render.shape

import net.minecraft.client.renderer.Tessellator.{instance => tess}
import org.lwjgl.opengl.GL11
import jk_5.worldeditcui.render.{PointRectangle, LineColor}
import scala.collection.mutable

/**
 * No description given
 *
 * @author jk-5
 */
class Render2DBox(val color: LineColor, val points: mutable.ArrayBuffer[PointRectangle], var min: Int = 0, var max: Int = 0) {

  def render(){
    import GL11._
    val off = 0.03D
    color.getColors.foreach(color => {
      color.prepareRender()
      tess.startDrawing(GL_LINES)
      color.prepareColor()
      points.filter(_ != null).foreach(p => {
        tess.addVertex(p.point.getX + 0.5, min + off, p.point.getY + 0.5)
        tess.addVertex(p.point.getX + 0.5, min + 1 + off, p.point.getY + 0.5)
      })
    })
    tess.draw()
  }
}
