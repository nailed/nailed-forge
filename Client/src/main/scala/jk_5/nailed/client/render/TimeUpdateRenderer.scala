package jk_5.nailed.client.render

import net.minecraft.client.gui.{Gui, FontRenderer}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraft.client.Minecraft.{getMinecraft => mc}

/**
 * No description given
 *
 * @author jk-5
 */
object TimeUpdateRenderer {
  var renderer: Option[FontRenderer] = None
  var format = ""
  val height = 13

  @SubscribeEvent def onRender(event: RenderGameOverlayEvent.Post) = event.`type` match {
    case ElementType.ALL => if(!format.isEmpty){
      if(renderer.isEmpty) renderer = Some(mc.fontRenderer)
      val rectWidth = renderer.get.getStringWidth(format) + 7
      Gui.drawRect(event.resolution.getScaledWidth - rectWidth, 0, event.resolution.getScaledWidth, this.height, 0x88000000)
      this.renderer.get.drawString(format, event.resolution.getScaledWidth - rectWidth + 3, 3, 0xFFFFFFFF)
    }
    case _ =>
  }
}
