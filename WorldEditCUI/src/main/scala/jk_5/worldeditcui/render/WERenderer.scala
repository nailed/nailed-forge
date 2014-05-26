package jk_5.worldeditcui.render

import org.lwjgl.opengl.GL11
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import jk_5.worldeditcui.render.region.{CuboidRegion, Region}

/**
 * No description given
 *
 * @author jk-5
 */
object WERenderer {

  var selection: Option[Region] = Some(new CuboidRegion)
  selection.get.initialize()

  @SubscribeEvent def onRender(event: RenderWorldLastEvent){
    if(this.selection.isEmpty) return
    import GL11._

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_BLEND)
    glDisable(GL_TEXTURE_2D)
    glDepthMask(false)
    glPushMatrix()
    try{
      val player = mc.renderViewEntity
      val dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks
      val dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks
      val dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks
      glTranslated(-dx, -dy, -dz)
      glColor3f(1, 1, 1)
      this.selection.get.render()
    }catch{
      case e: Exception =>
    }
    glDepthFunc(GL11.GL_LEQUAL)
    glPopMatrix()
    glDepthMask(true)
    glEnable(GL11.GL_TEXTURE_2D)
    glDisable(GL11.GL_BLEND)
  }
}
