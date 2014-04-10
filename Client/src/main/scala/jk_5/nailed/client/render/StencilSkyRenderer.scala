package jk_5.nailed.client.render

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import java.lang.reflect.Method
import cpw.mods.fml.relauncher.ReflectionHelper
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import org.lwjgl.opengl.GL11

/**
 * No description given
 *
 * @author jk-5
 */
object StencilSkyRenderer {

  var renderThisTick = false
  var renderMask: Int = -1
  var setupFogFailed = false
  val setupFogMethod: Option[Method] = try{
    Some(ReflectionHelper.findMethod(classOf[EntityRenderer], null, Array[String]("setupFog", "func_78468_a"), classOf[Integer], classOf[java.lang.Float]))
  }catch{
    case e: Throwable =>
      setupFogFailed = true
      None
  }

  def renderNextTick() = renderThisTick = true

  def setupFog(delta: Float){
    if(setupFogFailed) return
    try{
      setupFogMethod.get.invoke(mc.entityRenderer, -1: Integer, delta: java.lang.Float)
    }catch{
      case e: Throwable => //Oh well...
    }
  }

  @SubscribeEvent def onRender(event: RenderWorldLastEvent){
    if(!renderThisTick) return
    renderThisTick = false

    import GL11._

    glPushAttrib(GL_ALL_ATTRIB_BITS)
    glEnable(GL_STENCIL_TEST)
    glStencilMask(renderMask)
    glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP)
    glStencilFunc(GL_EQUAL, renderMask, renderMask)

    glDisable(GL_DEPTH_TEST)
    glDisable(GL_LIGHTING)

    setupFog(event.partialTicks)
    event.context.renderSky(event.partialTicks)

    glClearStencil(0)
    glClear(GL_STENCIL_BUFFER_BIT)
    glPopAttrib()
  }
}
