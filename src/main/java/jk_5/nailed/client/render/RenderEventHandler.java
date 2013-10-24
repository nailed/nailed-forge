package jk_5.nailed.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

/**
 * No description given
 *
 * @author jk-5
 */
public class RenderEventHandler {

    private Minecraft mc = Minecraft.getMinecraft();
    public static String format = "";

    @ForgeSubscribe
    public void onRenderOverlay(RenderGameOverlayEvent.Post event){
        if(event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if(format.isEmpty()) return;

        int rectWidth = mc.fontRenderer.getStringWidth(format) + 7;
        int rectHeight = 13;
        Gui.drawRect(event.resolution.getScaledWidth() - rectWidth, 0, event.resolution.getScaledWidth(), rectHeight, 0x88000000);
        mc.fontRenderer.drawString(format, event.resolution.getScaledWidth() - rectWidth + 3, 3, 0xFFFFFFFF);
    }
}
