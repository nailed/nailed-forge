package jk_5.worldeditcui.render.shape

import jk_5.worldeditcui.render.{LineInfo, PointCube, LineColor}
import jk_5.worldeditcui.render.vector.Vector3D
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.Tessellator.{instance => tess}
import jk_5.worldeditcui.WorldEditCUI

/**
 * No description given
 *
 * @author jk-5
 */
class RenderEllipsoid(val color: LineColor, val center: PointCube, val radii: Vector3D) {
  val centerX = center.point.getX + 0.5
  val centerY = center.point.getY + 0.5
  val centerZ = center.point.getZ + 0.5

  def render(){
    color.getColors.foreach(c => {
      c.prepareRender()
      drawXZPlane(c)
      drawYZPlane(c)
      drawXYPlane(c)
    })
  }

  protected def drawXZPlane(color: LineInfo) {
    val yRad = Math.floor(radii.getY).toInt
    for(ybl <- -yRad until yRad){
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()
      for(i <- 0 to 40){
        val theta = i * WorldEditCUI.TWOPI / 40
        val x = radii.getX * Math.cos(theta) * Math.cos(Math.asin(ybl / radii.getY))
        val z = radii.getZ * Math.sin(theta) * Math.cos(Math.asin(ybl / radii.getY))
        tess.addVertex(centerX + x, centerY + ybl, centerZ + z)
      }
      tess.draw()
    }
    tess.startDrawing(GL11.GL_LINE_LOOP)
    color.prepareColor()
    for(i <- 0 to 40){
      val theta = i * WorldEditCUI.TWOPI / 40
      val x = radii.getX * Math.cos(theta)
      val z = radii.getZ * Math.sin(theta)
      tess.addVertex(centerX + x, centerY, centerZ + z)
    }
    tess.draw
  }

  protected def drawYZPlane(color: LineInfo) {
    val xRad = Math.floor(radii.getX).toInt
    for(xbl <- -xRad until xRad){
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()
      for(i <- 0 to 40){
        val theta = i * WorldEditCUI.TWOPI / 40
        val y = radii.getY * Math.cos(theta) * Math.sin(Math.acos(xbl / radii.getX))
        val z = radii.getZ * Math.sin(theta) * Math.sin(Math.acos(xbl / radii.getX))
        tess.addVertex(centerX + xbl, centerY + y, centerZ + z)
      }
      tess.draw()
    }
    tess.startDrawing(GL11.GL_LINE_LOOP)
    color.prepareColor()
    for(i <- 0 to 40){
      val theta = i * WorldEditCUI.TWOPI / 40
      val y = radii.getY * Math.cos(theta)
      val z = radii.getZ * Math.sin(theta)
      tess.addVertex(centerX, centerY + y, centerZ + z)
    }
    tess.draw
  }

  protected def drawXYPlane(color: LineInfo) {
    val zRad = Math.floor(radii.getZ).toInt
    for(zbl <- -zRad until zRad){
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()
      for(i <- 0 to 40){
        val theta = i * WorldEditCUI.TWOPI / 40
        val x = radii.getX * Math.sin(theta) * Math.sin(Math.acos(zbl / radii.getZ))
        val y = radii.getY * Math.cos(theta) * Math.sin(Math.acos(zbl / radii.getZ))
        tess.addVertex(centerX + x, centerY + y, centerZ + zbl)
      }
      tess.draw()
    }
    tess.startDrawing(GL11.GL_LINE_LOOP)
    color.prepareColor()
    for(i <- 0 to 40){
      val theta = i * WorldEditCUI.TWOPI / 40
      val x = radii.getX * Math.cos(theta)
      val y = radii.getY * Math.sin(theta)
      tess.addVertex(centerX + x, centerY + y, centerZ)
    }
    tess.draw
  }
}
