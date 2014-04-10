package jk_5.nailed.client.render.tileentity

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.MinecraftForgeClient
import org.lwjgl.opengl.GL11
import net.minecraft.client.renderer.{EntityRenderer, Tessellator}
import jk_5.nailed.client.render.{StencilSkyRenderer, RenderUtils}
import net.minecraft.client.Minecraft.{getMinecraft => mc}
import cpw.mods.fml.relauncher.ReflectionHelper

/**
 * No description given
 *
 * @author jk-5
 */
object TileEntitySkyRenderer extends TileEntitySpecialRenderer {

  var disableStencil = false
  var initialized = false
  var displayListBase = -1

  override def renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, delta: Float){
    if(disableStencil) return
    if(!initialized){
      this.initialize()
      this.initialized = true
    }

    GL11.glPushMatrix()
    GL11.glTranslated(x, y, z)
    this.fogColor()
    GL11.glCallList(displayListBase + MinecraftForgeClient.getRenderPass)

    GL11.glPopMatrix()
    StencilSkyRenderer.renderNextTick()
  }

  def initialize(){
    val stencilBit = MinecraftForgeClient.reserveStencilBit()
    if(stencilBit >= 0){
      val mask = 1 << stencilBit
      displayListBase = GL11.glGenLists(2)

      GL11.glNewList(displayListBase, GL11.GL_COMPILE)
      renderCube()
      GL11.glEndList()

      GL11.glNewList(displayListBase + 1, GL11.GL_COMPILE)
      cutHoleInWorld(mask)
      GL11.glEndList()

      StencilSkyRenderer.renderMask = mask
    }
  }

  def renderCube(){
    val tes = new Tessellator
    tes.startDrawingQuads()

    tes.addVertex(0, 0, 0)
    tes.addVertex(0, 1, 0)
    tes.addVertex(1, 1, 0)
    tes.addVertex(1, 0, 0)

    tes.addVertex(0, 0, 1)
    tes.addVertex(1, 0, 1)
    tes.addVertex(1, 1, 1)
    tes.addVertex(0, 1, 1)

    tes.addVertex(0, 0, 0)
    tes.addVertex(0, 0, 1)
    tes.addVertex(0, 1, 1)
    tes.addVertex(0, 1, 0)

    tes.addVertex(1, 0, 0)
    tes.addVertex(1, 1, 0)
    tes.addVertex(1, 1, 1)
    tes.addVertex(1, 0, 1)

    tes.addVertex(0, 0, 0)
    tes.addVertex(1, 0, 0)
    tes.addVertex(1, 0, 1)
    tes.addVertex(0, 0, 1)

    tes.addVertex(0, 1, 0)
    tes.addVertex(0, 1, 1)
    tes.addVertex(1, 1, 1)
    tes.addVertex(1, 1, 0)

    GL11.glDisable(GL11.GL_LIGHTING)
    GL11.glDisable(GL11.GL_TEXTURE_2D)
    RenderUtils.disableLightmap()
    tes.draw()
    RenderUtils.enableLightmap()
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glEnable(GL11.GL_LIGHTING)
  }

  def cutHoleInWorld(mask: Int){
    GL11.glStencilMask(mask)
    GL11.glEnable(GL11.GL_STENCIL_TEST)
    GL11.glStencilFunc(GL11.GL_ALWAYS, mask, mask)
    GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE)
    GL11.glColorMask(false, false, false, false)
    renderCube()
    GL11.glColorMask(true, true, true, true)
    GL11.glStencilMask(0)
    GL11.glDisable(GL11.GL_STENCIL_TEST)
  }

  def fogColor(){
    try{
      val re = mc.entityRenderer
      val red = ReflectionHelper.getPrivateValue(classOf[EntityRenderer], re, "fogColorRed", "field_78518_n")
      val green = ReflectionHelper.getPrivateValue(classOf[EntityRenderer], re, "fogColorGreen", "field_78519_o")
      val blue = ReflectionHelper.getPrivateValue(classOf[EntityRenderer], re, "fogColorBlue", "field_78533_p")
      GL11.glColor3f(red, green, blue)
    }catch{
      case t: Throwable => GL11.glColor3f(1, 1, 1)
    }
  }
}
