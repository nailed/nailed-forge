package jk_5.nailed.client.render

import scala.util.Random
import net.minecraft.util.ResourceLocation
import net.minecraft.client.entity.AbstractClientPlayer
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.Tessellator
import scala.collection.mutable
import net.minecraft.client.Minecraft

/**
 * Created by matthias on 28-5-14.
 */
class PlayerHeadRenderer(name: String, xx: Int, yy: Int, random: Random) {
  var x: Int = xx
  var y: Int = yy
  var pitch: Double = 0
  var yaw: Double = 0
  var dPitch: Double = 0
  var dYaw: Double = 0
  var tex: ResourceLocation = getTexture(name)
  var mc: Minecraft = Minecraft.getMinecraft

  def this(name: String, random: Random) = this(name, 0, 0, random)

  def renderHead(){
    import GL11._

    mc.getTextureManager.bindTexture(this.tex)

    @inline val width = 16
    @inline val height = 16
    @inline val depth = 16
    @inline val hWidth: Int = width / 2
    @inline val hHeight: Int = height / 2
    @inline val hDepth: Int = depth / 2
    @inline val uMin_f = 0.125f // f = front
    @inline val vMin_f = 0.25f
    @inline val uMax_f = 0.25f
    @inline val vMax_f = 0.5f
    @inline val uMin_t = 0.125f // t = top
    @inline val vMin_t = 0.0f
    @inline val uMax_t = 0.25f
    @inline val vMax_t = 0.25f
    @inline val uMin_b = 0.25f // b = back
    @inline val vMin_b = 0.0f
    @inline val uMax_b = 0.375f
    @inline val vMax_b = 0.25f
    @inline val uMin_l = 0.0f // r = right
    @inline val vMin_l = 0.25f
    @inline val uMax_l = 0.125f
    @inline val vMax_l = 0.5f
    @inline val uMin_r = 0.25f // l = left
    @inline val vMin_r = 0.25f
    @inline val uMax_r = 0.375f
    @inline val vMax_r = 0.5f
    @inline val tessellator = Tessellator.instance

    glTranslated(this.x + hWidth, this.y + hHeight, hDepth)
    glRotated(pitch, 0, 1, 0)
    glRotated(yaw, 0, 0, 1)
    tessellator.setColorOpaque(255, 255, 255)
    tessellator.startDrawingQuads()
    tessellator.addVertexWithUV(- hWidth, hHeight, - hDepth, uMin_f, vMax_f)
    tessellator.addVertexWithUV(hWidth, hHeight, - hDepth, uMax_f, vMax_f)
    tessellator.addVertexWithUV(hWidth, -hHeight, -hDepth, uMax_f, vMin_f)
    tessellator.addVertexWithUV(- hWidth, -hHeight, -hDepth, uMin_f, vMin_f)

    tessellator.addVertexWithUV(-hWidth, -hHeight, hDepth, uMin_t, vMax_t)
    tessellator.addVertexWithUV(hWidth, -hHeight, hDepth, uMax_t, vMax_t)
    tessellator.addVertexWithUV(hWidth, -hHeight, -hDepth, uMax_t, vMin_t)
    tessellator.addVertexWithUV(-hWidth, -hHeight, -hDepth, uMin_t, vMin_t)

    tessellator.addVertexWithUV(-hWidth, hHeight, -hDepth, uMin_b, vMax_b)
    tessellator.addVertexWithUV(hWidth, hHeight, -hDepth, uMax_b, vMax_b)
    tessellator.addVertexWithUV(hWidth, hHeight, hDepth, uMax_b, vMin_b)
    tessellator.addVertexWithUV(-hWidth, hHeight, hDepth, uMin_b, vMin_b)

    tessellator.addVertexWithUV(-hWidth, hHeight, hDepth, uMin_l, vMax_l)
    tessellator.addVertexWithUV(-hWidth, hHeight, -hDepth, uMax_l, vMax_l)
    tessellator.addVertexWithUV(-hWidth, -hHeight, -hDepth, uMax_l, vMin_l)
    tessellator.addVertexWithUV(-hWidth, -hHeight, hDepth, uMin_l, vMin_l)

    tessellator.addVertexWithUV(hWidth, hHeight, -hDepth, uMin_r, vMax_r)
    tessellator.addVertexWithUV(hWidth, hHeight, hDepth, uMax_r, vMax_r)
    tessellator.addVertexWithUV(hWidth, -hHeight, hDepth, uMax_r, vMin_r)
    tessellator.addVertexWithUV(hWidth, -hHeight, -hDepth, uMin_r, vMin_r)
    tessellator.draw()

    glPushMatrix()
    glScalef(0.5f, 0.5f, 1)
    mc.fontRenderer.drawString(this.name, (x + 8 - (mc.fontRenderer.getStringWidth(this.name) / 4)) * 2, (y + 18) * 2, 0xFFFFFFFF: Int)
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
    @inline val tex = AbstractClientPlayer.getLocationSkin(username)
    AbstractClientPlayer.getDownloadImageSkin(tex, username)
    tex
  }

}
