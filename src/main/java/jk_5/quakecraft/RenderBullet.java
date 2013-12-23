package jk_5.quakecraft;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * No description given
 *
 * @author jk-5
 */
public class RenderBullet extends Render {

    private final ResourceLocation texture = new ResourceLocation("quakecraft", "textures/entities/bullet.png");

    public void doRenderEntityBullet(EntityBullet entity, double x, double y, double z, float f, float f1){
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.1F, 0.1F, 0.1F);
        this.bindTexture(this.texture);
        Tessellator var12 = Tessellator.instance;
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        var12.startDrawingQuads();
        var12.setNormal(0.0F, 1.0F, 0.0F);
        var12.addVertexWithUV(-0.5F, -0.25F, 0.0D, 0, 1);
        var12.addVertexWithUV(0.5F, -0.25F, 0.0D, 1, 1);
        var12.addVertexWithUV(0.5F, 0.75F, 0.0D, 1, 0);
        var12.addVertexWithUV(-0.5F, 0.75F, 0.0D, 0, 0);
        var12.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1){
        this.doRenderEntityBullet((EntityBullet) entity, d0, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity){
        return this.texture;
    }
}
