package jk_5.nailed.client.render

import scala.io.Source
import net.minecraft.client.renderer.texture.TextureManager
import jk_5.nailed.NailedLog
import net.minecraft.client.renderer.{Tessellator, GLAllocation}
import org.lwjgl.opengl.GL11
import jk_5.nailed.client.NailedClient
import net.minecraft.util.ResourceLocation
import jk_5.nailed.util.ColorFormat

/**
 * No description given
 *
 * @author jk-5
 */
object MonospaceFontRenderer {

  private val chars = Source.fromInputStream(MonospaceFontRenderer.getClass.getResourceAsStream("/assets/nailed/textures/terminal/chars.txt"))("UTF-8").mkString

  private var instance: Option[Renderer] = None

  private val charsAntiAliased = new ResourceLocation("nailed", "textures/terminal/chars.png")
  private val charsAliased = new ResourceLocation("nailed", "textures/terminal/chars_aliased.png")

  val fontWidth = 5
  val fontHeight = 9

  val charScale = NailedClient.getConfig.getTag("terminal").getTag("charScale").getDoubleValue(1.01)
  val antiAlias = NailedClient.getConfig.getTag("terminal").getTag("antialias").getBooleanValue(true)
  val linearFiltering = NailedClient.getConfig.getTag("terminal").getTag("linearFiltering").getBooleanValue(false)

  def pack(foreground: Int, background: Int) = ColorFormat.pack(foreground, background)
  def unpackForeground(color: Short) = ColorFormat.unpackForeground(color)
  def unpackBackground(color: Short) = ColorFormat.unpackBackground(color)

  def init(textureManager: TextureManager) = this.synchronized(instance = instance.orElse(Some(new Renderer(textureManager))))

  def drawString(x: Int, y: Int, value: Array[Char], color: Array[Short]) = this.synchronized(instance match {
    case None => NailedLog.warn("Trying to render string with uninitialized MonospaceFontRenderer.")
    case Some(renderer) => renderer.drawString(x, y, value, color)
  })

  private class Renderer(private val textureManager: TextureManager) {
    /** Display lists, one per char (renders quad with char's uv coords). */
    private val charLists = GLAllocation.generateDisplayLists(256)
    RenderHelper.checkError("MonospaceFontRenderer.charLists")

    /** Buffer filled with char display lists to efficiently draw strings. */
    private val listBuffer = GLAllocation.createDirectIntBuffer(512)
    RenderHelper.checkError("MonospaceFontRenderer.listBuffer")

    private val (charWidth, charHeight) = (MonospaceFontRenderer.fontWidth * 2, MonospaceFontRenderer.fontHeight * 2)
    private val cols = 256 / charWidth
    private val uStep = charWidth / 256.0
    private val uSize = uStep
    private val vStep = (charHeight + 1) / 256.0
    private val vSize = charHeight / 256.0

    // Set up the display lists.
    {
      val dw = charWidth * charScale - charWidth
      val dh = charHeight * charScale - charHeight
      val t = Tessellator.instance
      //Create display lists for al chars
      for(index <- 1 until 0xFF){
        val x = (index - 1) % cols
        val y = (index - 1) / cols
        val u = x * uStep
        val v = y * vStep
        GL11.glNewList(charLists + index, GL11.GL_COMPILE)
        t.startDrawingQuads()
        t.addVertexWithUV(-dw, charHeight * charScale, 0, u, v + vSize)
        t.addVertexWithUV(charWidth * charScale, charHeight * charScale, 0, u + uSize, v + vSize)
        t.addVertexWithUV(charWidth * charScale, -dh, 0, u + uSize, v)
        t.addVertexWithUV(-dw, -dh, 0, u, v)
        t.draw()
        GL11.glTranslatef(charWidth, 0, 0)
        GL11.glEndList()
      }
      // Special case for whitespace: just translate, don't render.
      GL11.glNewList(charLists + ' ', GL11.GL_COMPILE)
      GL11.glTranslatef(charWidth, 0, 0)
      GL11.glEndList()
    }

    def drawString(x: Int, y: Int, value: Array[Char], color: Array[Short]) = {
      if (color.length != value.length) throw new IllegalArgumentException("Color count must match char count.")

      if(antiAlias){
        textureManager.bindTexture(charsAntiAliased)
      }else{
        textureManager.bindTexture(charsAliased)
      }
      GL11.glPushMatrix()
      GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TEXTURE_BIT)
      GL11.glTranslatef(x, y, 0)
      GL11.glScalef(0.5f, 0.5f, 1)
      GL11.glDepthMask(false)
      GL11.glEnable(GL11.GL_TEXTURE_2D)

      // Background first. We try to merge adjacent backgrounds of the same
      // color to reduce the number of quads we have to draw.
      var cbg = 0x000000
      var offset = 0
      var width = 0
      for(col <- color.map(unpackBackground)){
        if (col != cbg) {
          draw(cbg, offset, width)
          cbg = col
          offset += width
          width = 0
        }
        width = width + 1
      }
      draw(cbg, offset, width)

      if(linearFiltering){
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
      }

      // Foreground second. We only have to flush when the color changes, so
      // unless every char has a different color this should be quite efficient.
      var cfg = -1
      for ((ch, col) <- value.zip(color.map(unpackForeground))) {
        val index = 1 + chars.indexOf(ch) match {
          case -1 => chars.indexOf('?')
          case i => i
        }
        if (col != cfg) {
          // Color changed, force flush and adjust colors.
          flush()
          cfg = col
          GL11.glColor3ub(
            ((cfg & 0xFF0000) >> 16).toByte,
            ((cfg & 0x00FF00) >> 8).toByte,
            ((cfg & 0x0000FF) >> 0).toByte)
        }
        listBuffer.put(charLists + index)
        if (listBuffer.remaining == 0)
          flush()
      }
      flush()

      GL11.glPopAttrib()
      GL11.glPopMatrix()
    }

    private val bgu1 = 254.0 / 256.0
    private val bgu2 = 255.0 / 256.0
    private val bgv1 = 255.0 / 256.0
    private val bgv2 = 256.0 / 256.0

    private def draw(color: Int, offset: Int, width: Int) = if (color != 0 && width > 0) {
      // IMPORTANT: we must not use the tessellator here. Doing so can cause
      // crashes on certain graphics cards with certain drivers (reported for
      // ATI/AMD and Intel chip sets). These crashes have been reported to
      // happen I have no idea why, and can only guess that it's related to
      // using the VBO/ARB the tessellator uses inside a display list (since
      // this stuff is eventually only rendered via display lists).
      GL11.glBegin(GL11.GL_QUADS)
      GL11.glColor3ub(((color >> 16) & 0xFF).toByte, ((color >> 8) & 0xFF).toByte, (color & 0xFF).toByte)
      GL11.glTexCoord2d(bgu1, bgv2)
      GL11.glVertex3d(charWidth * offset, charHeight, 0)
      GL11.glTexCoord2d(bgu2, bgv2)
      GL11.glVertex3d(charWidth * (offset + width), charHeight, 0)
      GL11.glTexCoord2d(bgu2, bgv1)
      GL11.glVertex3d(charWidth * (offset + width), 0, 0)
      GL11.glTexCoord2d(bgu1, bgv1)
      GL11.glVertex3d(charWidth * offset, 0, 0)
      GL11.glEnd()
    }

    private def flush() = if (listBuffer.position > 0) {
      listBuffer.flip()
      GL11.glCallLists(listBuffer)
      listBuffer.clear()
    }
  }
}
