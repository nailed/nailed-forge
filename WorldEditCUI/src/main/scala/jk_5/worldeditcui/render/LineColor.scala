package jk_5.worldeditcui.render

import org.lwjgl.opengl.GL11

/**
 * No description given
 *
 * @author jk-5
 */
object LineColor {
  final val CUBOIDGRID = new LineColor(0.8F, 0.2F, 0.2F)
  final val CUBOIDBOX = new LineColor(0.8F, 0.3F, 0.3F)
  final val CUBOIDPOINT1 = new LineColor(0.2F, 0.8F, 0.2F)
  final val CUBOIDPOINT2 = new LineColor(0.2F, 0.2F, 0.8F)
  final val POLYGRID = new LineColor(0.8F, 0.2F, 0.2F)
  final val POLYBOX = new LineColor(0.8F, 0.3F, 0.3F)
  final val POLYPOINT = new LineColor(0.2F, 0.8F, 0.8F)
  final val ELLIPSOIDGRID = new LineColor(0.8F, 0.3F, 0.3F)
  final val ELLIPSOIDCENTER = new LineColor(0.8F, 0.8F, 0.2F)
  final val CYLINDERGRID = new LineColor(0.8F, 0.2F, 0.2F)
  final val CYLINDERBOX = new LineColor(0.8F, 0.3F, 0.3F)
  final val CYLINDERCENTER = new LineColor(0.8F, 0.2F, 0.8F)
}

case class LineColor(r: Float, g: Float, b: Float) {
  var normal = new LineInfo(3, r, g, b, 0.8f, GL11.GL_LESS)
  var hidden = new LineInfo(3, r, g, b, 0.2f, GL11.GL_GEQUAL)

  def setColor(hex: String){
    val r = Integer.parseInt(hex.substring(1, 3), 16)
    val g = Integer.parseInt(hex.substring(3, 5), 16)
    val b = Integer.parseInt(hex.substring(5, 7), 16)

    val rF = r.floatValue / 256.0F
    val gF = g.floatValue / 256.0F
    val bF = b.floatValue / 256.0F

    normal = new LineInfo(3.0f, rF, gF, bF, 0.8f, GL11.GL_LESS)
    hidden = new LineInfo(3.0f, rF, gF, bF, 0.2f, GL11.GL_GEQUAL)
  }

  def getColors = Array(normal, hidden)
}

case class LineInfo(lineWidth: Float, r: Float, g: Float, b: Float, a: Float, depthfunc: Int){

  def prepareRender(){
    GL11.glLineWidth(lineWidth)
    GL11.glDepthFunc(depthfunc)
  }

  def prepareColor(){
    GL11.glColor4f(r, g, b, a)
  }
}
