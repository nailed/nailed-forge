package jk_5.nailed.client.render;

import org.lwjgl.opengl.*;

import net.minecraft.client.renderer.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class RenderUtils {

    private RenderUtils(){

    }

    public static void disableLightmap() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void enableLightmap() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
