package jk_5.nailed.client.render;

import net.minecraft.client.renderer.Tessellator;

/**
 * No description given
 *
 * @author jk-5
 */
public class RenderUtils {

    public static void drawTexturedQuad(int par1, int par2, int par3, int par4, int par5, int par6, double zLevel){
        float var7 = 0.0039063F;
        float var8 = 0.0039063F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(par1, par2 + par6, zLevel, (par3) * var7, (par4 + par6) * var8);
        var9.addVertexWithUV(par1 + par5, par2 + par6, zLevel, (par3 + par5) * var7, (par4 + par6) * var8);
        var9.addVertexWithUV(par1 + par5, par2, zLevel, (par3 + par5) * var7, par4 * var8);
        var9.addVertexWithUV(par1, par2, zLevel, (par3) * var7, (par4) * var8);
        var9.draw();
    }
}
