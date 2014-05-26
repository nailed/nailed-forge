package jk_5.worldeditcui.render.shape

import net.minecraft.client.renderer.Tessellator.{instance => tess}
import org.lwjgl.opengl.GL11
import jk_5.worldeditcui.render.{LineColor, PointCube}
import jk_5.worldeditcui.WorldEditCUI

/**
 * No description given
 *
 * @author jk-5
 */
class RenderCylinderBox(val color: LineColor, val center: PointCube, val radX: Double, val radZ: Double, val minY: Int, val maxY: Int) {
  val centerX = center.point.getX + 0.5
  val centerZ = center.point.getZ + 0.5

  def render(){
    color.getColors.foreach(color => {
      color.prepareRender()
      Array(minY, maxY + 1).foreach(ybl => {
        tess.startDrawing(GL11.GL_LINE_LOOP)
        color.prepareColor()
        for(i <- 0 to 75){
          val tempTheta: Double = i * WorldEditCUI.TWOPI / 75
          val tempX: Double = radX * Math.cos(tempTheta)
          val tempZ: Double = radZ * Math.sin(tempTheta)
          tess.addVertex(centerX + tempX, ybl, centerZ + tempZ)
        }
        tess.draw()
      })
    })
  }
}
