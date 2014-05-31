package jk_5.nailed.client.render

import scala.util.Random
import net.minecraft.util.ResourceLocation
import net.minecraft.client.entity.AbstractClientPlayer
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.Minecraft
import scala.collection.parallel.mutable

/**
 * Created by matthias on 28-5-14.
 */
class PlayerHeadRenderer(name: String, xx: Double, yy: Double, random: Random) {
  var x: Double = xx
  var y: Double = yy
  var pitch: Double = 0
  var yaw: Double = 0
  var dPitch: Double = 0
  var dYaw: Double = 0
  var hasTexture: Boolean = false
  var tex: ResourceLocation = null
  var mc: Minecraft = Minecraft.getMinecraft

  def this(name: String, random: Random) = this(name, 0, 0, random)

  def renderHead(){
    import GL11._
    if (!this.hasTexture) {
      this.tex = this.getTexture(this.name)
      this.hasTexture = true
    }
    mc.getTextureManager.bindTexture(this.tex)

    val width = 16
    val height = 16
    val depth = 16
    val hWidth: Int = width / 2
    val hHeight: Int = height / 2
    val hDepth: Int = depth / 2
    val uMin_f = 0.125f // f = front
    val vMin_f = 0.25f
    val uMax_f = 0.25f
    val vMax_f = 0.5f
    val uMin_t = 0.125f // t = top
    val vMin_t = 0.0f
    val uMax_t = 0.25f
    val vMax_t = 0.25f
    val uMin_b = 0.25f // b = back
    val vMin_b = 0.0f
    val uMax_b = 0.375f
    val vMax_b = 0.25f
    val uMin_l = 0.0f // r = right
    val vMin_l = 0.25f
    val uMax_l = 0.125f
    val vMax_l = 0.5f
    val uMin_r = 0.25f // l = left
    val vMin_r = 0.25f
    val uMax_r = 0.375f
    val vMax_r = 0.5f
    val uMin_a = 0.375f
    val vMax_a = 0.25f
    val uMax_a = 0.5f
    val vMin_a = 0.5f
    val tessellator = Tessellator.instance

    // implement translations

    val p1x = translateX(-hWidth, -hHeight, -hDepth, pitch, yaw) + this.x + 8 // back down left
    val p1y = translateY(-hWidth, -hHeight, -hDepth, pitch, yaw) + this.y + 8

    val p2x = translateX(hWidth, -hHeight, -hDepth, pitch, yaw) + this.x + 8 // back down right
    val p2y = translateY(hWidth, -hHeight, -hDepth, pitch, yaw) + this.y + 8

    val p3x = translateX(hWidth, -hHeight, hDepth, pitch, yaw) + this.x + 8 // front down right
    val p3y = translateY(hWidth, -hHeight, hDepth, pitch, yaw) + this.y + 8

    val p4x = translateX(-hWidth, -hHeight, hDepth, pitch, yaw) + this.x + 8 // front down left
    val p4y = translateY(-hWidth, -hHeight, hDepth, pitch, yaw) + this.y + 8

    val p5x = translateX(-hWidth, hHeight, -hDepth, pitch, yaw) + this.x + 8 // back up left
    val p5y = translateY(-hWidth, hHeight, -hDepth, pitch, yaw) + this.y + 8

    val p6x = translateX(hWidth, hHeight, -hDepth, pitch, yaw) + this.x + 8 // back up right
    val p6y = translateY(hWidth, hHeight, -hDepth, pitch, yaw) + this.y + 8

    val p7x = translateX(hWidth, hHeight, hDepth, pitch, yaw) + this.x + 8 // front up right
    val p7y = translateY(hWidth, hHeight, hDepth, pitch, yaw) + this.y + 8

    val p8x = translateX(-hWidth, hHeight, hDepth, pitch, yaw) + this.x + 8 // front up left
    val p8y = translateY(-hWidth, hHeight, hDepth, pitch, yaw) + this.y + 8

    glPushMatrix()
    tessellator.setColorOpaque(255, 255, 255)
    tessellator.startDrawingQuads()

    tessellator.addVertexWithUV(p8x, p8y, 0, uMin_f, vMax_f)
    tessellator.addVertexWithUV(p7x, p7y, 0, uMax_f, vMax_f)
    tessellator.addVertexWithUV(p3x, p3y, 0, uMax_f, vMin_f)
    tessellator.addVertexWithUV(p4x, p4y, 0, uMin_f, vMin_f)

    tessellator.addVertexWithUV(p4x, p4y, 0, uMin_t, vMax_t)
    tessellator.addVertexWithUV(p3x, p3y, 0, uMax_t, vMax_t)
    tessellator.addVertexWithUV(p2x, p2y, 0, uMax_t, vMin_t)
    tessellator.addVertexWithUV(p1x, p1y, 0, uMin_t, vMin_t)

    tessellator.addVertexWithUV(p5x, p5y, 0, uMin_b, vMax_b)
    tessellator.addVertexWithUV(p6x, p6y, 0, uMax_b, vMax_b)
    tessellator.addVertexWithUV(p7x, p7y, 0, uMax_b, vMin_b)
    tessellator.addVertexWithUV(p8x, p8y, 0, uMin_b, vMin_b)

    tessellator.addVertexWithUV(p5x, p5y, 0, uMin_l, vMax_l)
    tessellator.addVertexWithUV(p8x, p8y, 0, uMax_l, vMax_l)
    tessellator.addVertexWithUV(p4x, p4y, 0, uMax_l, vMin_l)
    tessellator.addVertexWithUV(p1x, p1y, 0, uMin_l, vMin_l)

    tessellator.addVertexWithUV(p7x, p7y, 0, uMin_r, vMax_r)
    tessellator.addVertexWithUV(p6x, p6y, 0, uMax_r, vMax_r)
    tessellator.addVertexWithUV(p2x, p2y, 0, uMax_r, vMin_r)
    tessellator.addVertexWithUV(p3x, p3y, 0, uMin_r, vMin_r)

    tessellator.addVertexWithUV(p6x, p6y, 0, uMin_a, vMax_a)
    tessellator.addVertexWithUV(p5x, p5y, 0, uMax_a, vMax_a)
    tessellator.addVertexWithUV(p1x, p1y, 0, uMax_a, vMin_a)
    tessellator.addVertexWithUV(p2x, p2y, 0, uMin_a, vMin_a)

    tessellator.draw()
    glPopMatrix()
    glPushMatrix()
    glScalef(0.5f, 0.5f, 1)
    mc.fontRenderer.drawString(this.name, (x + 8 - (mc.fontRenderer.getStringWidth(this.name) / 4)).toInt * 2, (y + 18).toInt * 2, 0xFFFFFFFF: Int)
    glPopMatrix()
    this.tick()
  }

  def tick(){
    this.pitch += this.dPitch
    this.yaw += this.dYaw
    this.dPitch = (this.dPitch + (if (random.nextBoolean()) -Math.random() else Math.random())) * Math.pow(1 - Math.abs(this.yaw) / 45, 2) // randomizing yaw movement
    this.dYaw = (this.dYaw + (if (random.nextBoolean()) -Math.random() else Math.random())) * Math.pow(1 - Math.abs(this.yaw) / 45, 2) // randomizing pitch movement
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

  def translateX(x: Int, y: Int, z: Int, pitch: Double, yaw: Double): Double = (x * Math.cos(Math.toRadians(pitch)) + (y * Math.sin(Math.toRadians(yaw)) + z * Math.cos(Math.toRadians(yaw))) * Math.sin(Math.toRadians(pitch))).toDouble

  def translateY(x: Int, y: Int, z: Int, pitch: Double, yaw: Double): Double = (y * Math.cos(Math.toRadians(yaw)) - z * Math.sin(Math.toRadians(yaw))).toDouble

}