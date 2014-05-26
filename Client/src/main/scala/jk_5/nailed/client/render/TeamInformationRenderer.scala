package jk_5.nailed.client.render

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraft.client.gui.{ScaledResolution, Gui}
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
    if(false) return
    val team1 = List("jk_5", "Dabadooba", "ikzelf1248")
    val team2 = List("Dinnerbone", "Grumm", "Jeb_")
    val team3 = List("Docm77","Etho","AnderZEL")
    renderTopRight(team1, event.resolution)
    renderLeft(team2, event.resolution)
    renderRight(team3, event.resolution)
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

  def renderTopMiddle(players: List[String], resolution: ScaledResolution) {
    import GL11._

    val start = resolution.getScaledWidth / 7
    val end = start * 6

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)

    for(name <- players){
      this.renderHead(start + number*31 + 8, 3, players(number))
      number = number + 1
    }

    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderTopRight(players: List[String], resolution: ScaledResolution) {
    import GL11._

    val start = resolution.getScaledWidth / 7
    val end = start * 3

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(name <- players){
      this.renderHead(start + number*31 + 8, 3, players(number))
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderTopLeft(players: List[String], resolution: ScaledResolution) {
    import GL11._

    val f = resolution.getScaledWidth / 7
    val start = f * 4
    val end = f * 6

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(name <- players){
      this.renderHead(start + number*31 + 8, 3, players(number))
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderLeft(players: List[String], resolution: ScaledResolution) {
    import GL11._

    val f = resolution.getScaledHeight / 7
    val start = f * 1
    val end = f * 5

    Gui.drawRect(0, start, 36, end, 0x88000000)

    val height = end - start

    players.take(height / 31)

    var number = 0

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(name <- players){
      this.renderHead(8, start + number*31 + 3, players(number))
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderRight(players: List[String], resolution: ScaledResolution) {
    import GL11._

    val f = resolution.getScaledHeight / 7
    val start = f * 1
    val end = f * 5
    val screenwidth = resolution.getScaledWidth

    Gui.drawRect(screenwidth - 36, start, screenwidth, end, 0x88000000)

    val height = end - start

    players.take(height / 31)

    var number = 0

    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(name <- players){
      this.renderHead(screenwidth - 28, start + 31*number + 3, players(number))
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }
}
