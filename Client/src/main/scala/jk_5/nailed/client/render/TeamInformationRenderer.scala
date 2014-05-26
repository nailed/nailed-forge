package jk_5.nailed.client.render

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraft.client.gui.Gui
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import scala.collection.mutable
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.Tessellator

/**
 * No description given
 *
 * @author jk-5
 */
object TeamInformationRenderer extends Gui {

  val textures = mutable.HashMap[String, ResourceLocation]()

  @SubscribeEvent def render(event: RenderGameOverlayEvent.Post): Unit = if(event.`type` == ElementType.ALL) {
    import GL11._
    if(true) return

    val start1 = event.resolution.getScaledWidth / 7
    val end1 = start1 * 3
    val start2 = start1 * 4
    val end2 = start1 * 6

    Gui.drawRect(start1, 0, end1, 28, 0x88000000)
    Gui.drawRect(start2, 0, end2, 28, 0x88000000)

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    glColor4f(1, 1, 1, 1)

    renderHead(start1 + 0*31 + 8, 3, "Clank26")
    renderHead(start1 + 1*31 + 8, 3, "Dabadooba")
    renderHead(start1 + 2*31 + 8, 3, "ikzelf1248")
    renderHead(start2 + 0*15 + 8, 3, "Dinnerbone")
    renderHead(start2 + 1*31 + 8, 3, "Notch")
    renderHead(start2 + 2*31 + 8, 3, "Grumm")

    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderHead(x: Int, y: Int, username: String){
    import GL11._

    mc.getTextureManager.bindTexture(this.getTexture(username))

    @inline val width = 16
    @inline val height = 16
    @inline val uMin = 0.125f
    @inline val vMin = 0.25f
    @inline val uMax = 0.25f
    @inline val vMax = 0.5f
    @inline val tessellator = Tessellator.instance

    tessellator.setColorOpaque(255, 255, 255)
    tessellator.startDrawingQuads()
    tessellator.addVertexWithUV(x, y + height, 0, uMin, vMax)
    tessellator.addVertexWithUV(x + width, y + height, 0, uMax, vMax)
    tessellator.addVertexWithUV(x + width, y, 0, uMax, vMin)
    tessellator.addVertexWithUV(x, y, 0, uMin, vMin)
    tessellator.draw()

    glPushMatrix()
    glScalef(0.5f, 0.5f, 1)
    mc.fontRenderer.drawString(username, (x + 8 - (mc.fontRenderer.getStringWidth(username) / 4)) * 2, (y + 18) * 2, 0xFFFFFFFF)
    glPopMatrix()
  }

  def getTexture(username: String): ResourceLocation = {
    if(textures.contains(username)) return textures.get(username).get
    val tex = AbstractClientPlayer.getLocationSkin(username)
    AbstractClientPlayer.getDownloadImageSkin(tex, username)
    textures.put(username, tex)
    tex
  }
}
