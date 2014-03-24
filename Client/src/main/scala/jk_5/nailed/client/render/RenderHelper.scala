package jk_5.nailed.client.render

import org.lwjgl.opengl.GL11
import jk_5.nailed.NailedLog
import org.lwjgl.util.glu.GLU

/**
 * No description given
 *
 * @author jk-5
 */
object RenderHelper {

  def checkError(where: String){
    val error = GL11.glGetError
    if(error != 0){
      NailedLog.warn("GL ERROR @ " + where + ": " + GLU.gluErrorString(error))
    }
  }
}
