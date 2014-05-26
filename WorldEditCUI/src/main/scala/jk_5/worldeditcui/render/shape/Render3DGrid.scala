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
class Render3DGrid(val color: LineColor, val first: Vector3D, val second: Vector3D) {

  def render(){
    val x1 = first.getX
    val y1 = first.getY
    val z1 = first.getZ
    val x2 = second.getX
    val y2 = second.getY
    val z2 = second.getZ
    color.getColors.foreach(color => {
      color.prepareRender()
      tess.startDrawing(GL11.GL_LINE_LOOP)
      color.prepareColor()

      var x, y, z, offset = 0D

      // Zmax XY plane, y axis
      z = z2
      y = y1
      val msize = 150
      if(y2 - y / offset < msize){
        var yoff = 0D
        while(yoff + y <= y2){
          tess.addVertex(x1, y + yoff, z)
          tess.addVertex(x2, y + yoff, z)
          yoff += offset
        }
      }

      // Zmin XY plane, y axis
      z = z1
      if(y2 - y / offset < msize){
        var yoff = 0D
        while (yoff + y <= y2) {
          tess.addVertex(x1, y + yoff, z)
          tess.addVertex(x2, y + yoff, z)
          yoff += offset
        }
      }

      // Xmin YZ plane, y axis
      x = x1
      if(y2 - y / offset < msize){
        var yoff = 0D
        while(yoff + y <= y2){
          tess.addVertex(x, y + yoff, z1)
          tess.addVertex(x, y + yoff, z2)
          yoff += offset
        }
      }

      // Xmax YZ plane, y axis
      x = x2
      if(y2 - y / offset < msize){
        var yoff = 0D
        while(yoff + y <= y2){
          tess.addVertex(x, y + yoff, z1)
          tess.addVertex(x, y + yoff, z2)
          yoff += offset
        }
      }

      // Zmin XY plane, x axis
      x = x1
      z = z1
      if(x2 - x / offset < msize){
        var xoff = 0D
        while(xoff + x <= x2){
          tess.addVertex(x + xoff, y1, z)
          tess.addVertex(x + xoff, y2, z)
          xoff += offset
        }
      }

      // Zmax XY plane, x axis
      z = z2
      if(x2 - x / offset < msize){
        var xoff = 0D
        while(xoff + x <= x2){
          tess.addVertex(x + xoff, y1, z)
          tess.addVertex(x + xoff, y2, z)
          xoff += offset
        }
      }

      // Ymin XZ plane, x axis
      y = y2
      if(x2 - x / offset < msize){
        var xoff = 0D
        while(xoff + x <= x2){
          tess.addVertex(x + xoff, y, z1)
          tess.addVertex(x + xoff, y, z2)
          xoff += offset
        }
      }

      // Ymax XZ plane, x axis
      y = y1
      if(x2 - x / offset < msize){
        var xoff = 0D
        while(xoff + x <= x2){
          tess.addVertex(x + xoff, y, z1)
          tess.addVertex(x + xoff, y, z2)
          xoff += offset
        }
      }

      // Ymin XZ plane, z axis
      z = z1
      y = y1
      if(z2 - z / offset < msize){
        var zoff = 0D
        while(zoff + z <= z2){
          tess.addVertex(x1, y, z + zoff)
          tess.addVertex(x2, y, z + zoff)
          zoff += offset
        }
      }

      // Ymax XZ plane, z axis
      y = y2
      if(z2 - z / offset < msize){
        var zoff = 0D
        while(zoff + z <= z2){
          tess.addVertex(x1, y, z + zoff)
          tess.addVertex(x2, y, z + zoff)
          zoff += offset
        }
      }

      // Xmin YZ plane, z axis
      x = x2
      if(z2 - z / offset < msize){
        var zoff = 0D
        while(zoff + z <= z2){
          tess.addVertex(x, y1, z + zoff)
          tess.addVertex(x, y2, z + zoff)
          zoff += offset
        }
      }

      // Xmax YZ plane, z axis
      x = x1
      if(z2 - z / offset < msize){
        var zoff = 0D
        while(zoff + z <= z2){
          tess.addVertex(x, y1, z + zoff)
          tess.addVertex(x, y2, z + zoff)
          zoff += offset
        }
      }

      tess.draw()
    })
  }
}
