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
import net.minecraft.client.renderer.Tessellator.{instance => tess}
import scala.util.Random
import java.util
import jk_5.nailed.map.teamlist.TeamInfo

/**
 * No description given
 *
 * @author jk-5
 */
object TeamInformationRenderer {

  var display = false

  val random = new Random()

  val left = mutable.ArrayBuffer(getRenderer("jk_5"), getRenderer("Dabadooba"), getRenderer("ikzelf1248"))
  val topLeft = mutable.ArrayBuffer[PlayerHeadRenderer]()
  val top = mutable.ArrayBuffer[PlayerHeadRenderer]()
  val topRight = mutable.ArrayBuffer(getRenderer("Dinnerbone"), getRenderer("Grumm"), getRenderer("Jeb_"))
  val right = mutable.ArrayBuffer(getRenderer("Docm77"),getRenderer("Etho"),getRenderer("AnderZEL"))

  val textures = mutable.HashMap[String, ResourceLocation]()

  @SubscribeEvent def render(event: RenderGameOverlayEvent.Post): Unit = if(event.`type` == ElementType.ALL) {
    if(!display) return
    renderTopRight(topRight, event.resolution)
    renderTopLeft(topLeft, event.resolution)
    renderTopMiddle(top, event.resolution)
    renderLeft(left, event.resolution)
    renderRight(right, event.resolution)
  }

  @inline def getRenderer(name: String) = new PlayerHeadRenderer(name, random)

  def renderHead(x: Int, y: Int, username: String){
    import GL11._

    mc.getTextureManager.bindTexture(this.getTexture(username))

    val width = 16
    val height = 16
    val uMin = 0.125f
    val vMin = 0.25f
    val uMax = 0.25f
    val vMax = 0.5f

    tess.setColorOpaque(255, 255, 255)
    tess.startDrawingQuads()
    tess.addVertexWithUV(x, y + height, 0, uMin, vMax)
    tess.addVertexWithUV(x + width, y + height, 0, uMax, vMax)
    tess.addVertexWithUV(x + width, y, 0, uMax, vMin)
    tess.addVertexWithUV(x, y, 0, uMin, vMin)
    tess.draw()

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

  def renderTopMiddle(players: mutable.ArrayBuffer[PlayerHeadRenderer], resolution: ScaledResolution) {
    if(players.isEmpty) return
    import GL11._

    val start = resolution.getScaledWidth / 7
    val end = start * 6

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glColor4f(1, 1, 1, 1)
    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(renderer <- players){
      renderer.setLocation(start + number*31 + 8, 3)
      renderer.renderHead()
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderTopLeft(players: mutable.ArrayBuffer[PlayerHeadRenderer], resolution: ScaledResolution) {
    if(players.isEmpty) return
    import GL11._

    val start = resolution.getScaledWidth / 7
    val end = start * 3

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glColor4f(1, 1, 1, 1)
    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(renderer <- players){
      renderer.setLocation(start + number*31 + 8, 3)
      renderer.renderHead()
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderTopRight(players: mutable.ArrayBuffer[PlayerHeadRenderer], resolution: ScaledResolution) {
    if(players.isEmpty) return
    import GL11._

    val f = resolution.getScaledWidth / 7
    val start = f * 4
    val end = f * 6

    Gui.drawRect(start, 0, end, 28, 0x88000000)

    val width = end - start

    players.take(width / 31)

    var number = 0

    glColor4f(1, 1, 1, 1)
    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(renderer <- players){
      renderer.setLocation(start + number*31 + 8, 3)
      renderer.renderHead()
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderLeft(players: mutable.ArrayBuffer[PlayerHeadRenderer], resolution: ScaledResolution) {
    if(players.isEmpty) return
    import GL11._

    val f = resolution.getScaledHeight / 7
    val start = f * 1
    val end = f * 5

    Gui.drawRect(0, start, 31, end, 0x88000000)

    val height = end - start

    players.take(height / 31)

    var number = 0

    glColor4f(1, 1, 1, 1)
    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(renderer <- players){
      renderer.setLocation(8, start + number*31 + 3)
      renderer.renderHead()
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def renderRight(players: mutable.ArrayBuffer[PlayerHeadRenderer], resolution: ScaledResolution) {
    if(players.isEmpty) return
    import GL11._

    val f = resolution.getScaledHeight / 7
    val start = f * 1
    val end = f * 5
    val screenwidth = resolution.getScaledWidth

    Gui.drawRect(screenwidth - 31, start, screenwidth, end, 0x88000000)

    val height = end - start

    players.take(height / 31)

    var number = 0

    glColor4f(1, 1, 1, 1)
    glPushMatrix()
    glEnable(GL_TEXTURE_2D)
    for(renderer <- players){
      renderer.setLocation(screenwidth - 23, start + 31*number + 3)
      renderer.renderHead()
      number += 1
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def setTeamInfo(list: util.List[TeamInfo]){
    clearAll()
    list.size() match {
      case 1 => top ++= teamInfoToHeadRenderList(list.get(0))
      case 2 =>
        topLeft ++= teamInfoToHeadRenderList(list.get(0))
        topRight ++= teamInfoToHeadRenderList(list.get(1))
      case 3 =>
        left ++= teamInfoToHeadRenderList(list.get(0))
        top ++= teamInfoToHeadRenderList(list.get(1))
        right ++= teamInfoToHeadRenderList(list.get(2))
      case 4 =>
        left ++= teamInfoToHeadRenderList(list.get(0))
        topLeft ++= teamInfoToHeadRenderList(list.get(1))
        topRight ++= teamInfoToHeadRenderList(list.get(2))
        right ++= teamInfoToHeadRenderList(list.get(3))
      case 5 =>
        left ++= teamInfoToHeadRenderList(list.get(0))
        topLeft ++= teamInfoToHeadRenderList(list.get(1))
        top ++= teamInfoToHeadRenderList(list.get(2))
        topRight ++= teamInfoToHeadRenderList(list.get(3))
        right ++= teamInfoToHeadRenderList(list.get(4))
    }
  }

  @inline def teamInfoToHeadRenderList(info: TeamInfo) = {
    val ret = mutable.ArrayBuffer[PlayerHeadRenderer]()
    info.players.foreach(p => ret += getRenderer(p))
    ret
  }

  def clearAll(){
    left.clear()
    topLeft.clear()
    top.clear()
    topRight.clear()
    right.clear()
  }
}
