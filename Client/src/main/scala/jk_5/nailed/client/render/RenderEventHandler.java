package jk_5.nailed.client.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * No description given
 *
 * @author jk-5
 */
public class RenderEventHandler {

    private Minecraft mc = Minecraft.getMinecraft();
    public static String format = "";
    public static boolean doRender = false;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event){
        if(event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if(format.isEmpty() || !doRender) return;

        int rectWidth = mc.fontRenderer.getStringWidth(format) + 7;
        int rectHeight = 13;
        Gui.drawRect(event.resolution.getScaledWidth() - rectWidth, 0, event.resolution.getScaledWidth(), rectHeight, 0x88000000);
        mc.fontRenderer.drawString(format, event.resolution.getScaledWidth() - rectWidth + 3, 3, 0xFFFFFFFF);
    }
}
