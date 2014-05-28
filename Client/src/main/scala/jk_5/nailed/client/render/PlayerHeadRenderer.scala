package jk_5.nailed.client.render

import scala.util.Random
import net.minecraft.util.ResourceLocation
import net.minecraft.client.entity.AbstractClientPlayer
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.Tessellator

/**
 * Created by matthias on 28-5-14.
 */
class PlayerHeadRenderer {
  private var name: String
  private var pitch: Double = 0
  private var yaw: Double = 0
  private var dPitch: Int
  private var dYaw: Int
  private var x: Int
  private var y: Int
  private var random: Random
  private var tex: ResourceLocation

  PlayerHeadRenderer(name: String, random: Random){
    this.name = name
    this.random = random
    this.getTexture(name)
  }

  PlayerHeadRenderer(name: String, x: Int, y: Int, random: Random){
    this.x = x
    this.y = y
    this.name = name
    this.dPitch = 0
    this.dYaw = 0
    this.random = random
    this.getTexture(name)
  }

  def renderHead(){
    import GL11._

    mc.getTextureManager.bindTexture(this.tex)

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
    mc.fontRenderer.drawString(this.name, (x + 8 - (mc.fontRenderer.getStringWidth(this.name) / 4)) * 2, (y + 18) * 2, 0xFFFFFFFF)
    glPopMatrix()
    this.tick()
  }

  def tick(){
    this.pitch += this.dPitch
    this.yaw += this.dYaw
    this.dPitch = (this.dPitch + (if (random.nextBoolean()) Math.random() else Math.random())) * Math.pow(1 - Math.abs(this.yaw) / 45, 2) // randomizing yaw movement
    this.dYaw = (this.dYaw + (if (random.nextBoolean()) Math.random() else Math.random())) * Math.pow(1 - Math.abs(this.yaw) / 45, 2) // randomizing pitch movement
  }

  def setLocation(x: Int, y: Int){
    this.x = x
    this.y = y
  }

  def getTexture(username: String): ResourceLocation = {
    if(textures.contains(username)) return textures.get(username).get
    val tex = AbstractClientPlayer.getLocationSkin(username)
    AbstractClientPlayer.getDownloadImageSkin(tex, username)
    this.tex = tex
    tex
  }

}
