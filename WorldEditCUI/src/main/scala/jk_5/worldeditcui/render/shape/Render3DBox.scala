package jk_5.worldeditcui.render.shape

import net.minecraft.client.renderer.Tessellator.{instance => tess}
import org.lwjgl.opengl.GL11
import jk_5.worldeditcui.render.LineColor
import jk_5.worldeditcui.render.vector.Vector3D

/**
 * No description given
 *
 * @author jk-5
 */
class Render3DBox(val color: LineColor, val first: Vector3D, val second: Vector3D) {

  def render(){
    val x1 = first.getX
    val y1 = first.getY
    val z1 = first.getZ
    val x2 = second.getX
    val y2 = second.getY
    val z2 = second.getZ
    color.getColors.foreach(color => {
      color.prepareRender()

      // Draw bottom face
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()
      tess.addVertex(x1, y1, z1)
      tess.addVertex(x2, y1, z1)
      tess.addVertex(x2, y1, z2)
      tess.addVertex(x1, y1, z2)
      tess.draw()

      // Draw top face
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()
      tess.addVertex(x1, y2, z1)
      tess.addVertex(x2, y2, z1)
      tess.addVertex(x2, y2, z2)
      tess.addVertex(x1, y2, z2)
      tess.draw()

      // Draw join top and bottom faces
      tess.startDrawing(GL11.GL_LINES)
      tess.addVertex(x1, y1, z1)
      tess.addVertex(x1, y2, z1)
      tess.addVertex(x2, y1, z1)
      tess.addVertex(x2, y2, z1)
      tess.addVertex(x2, y1, z2)
      tess.addVertex(x2, y2, z2)
      tess.addVertex(x1, y1, z2)
      tess.addVertex(x1, y2, z2)
      tess.draw()
    })
  }
}
