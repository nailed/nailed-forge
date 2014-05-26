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
class Render2DGrid(val color: LineColor, val points: mutable.ArrayBuffer[PointRectangle], var min: Int = 0, var max: Int = 0) {

  def render(){
    for(d <- min to max + 1){
      import GL11._
      color.getColors.foreach(color => {
        color.prepareRender()
        tess.startDrawing(GL_LINE_LOOP)
        color.prepareColor()
        points.filter(_ != null).foreach(p => {
          tess.addVertex(p.point.getX + 0.5, d + 0.03D, p.point.getY + 0.5)
        })
      })
      tess.draw()
    }
  }
}
