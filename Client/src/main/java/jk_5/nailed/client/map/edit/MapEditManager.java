package jk_5.nailed.client.map.edit;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.map.Spawnpoint;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapEditManager {

    private static final MapEditManager INSTANCE = new MapEditManager();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final ResourceLocation noise = new ResourceLocation("nailed", "textures/effects/noise.png");

    @Setter private boolean enabled;
    @Getter private MapEditMetadata metadata = new MapEditMetadata();

    public void readData(ByteBuf buffer){
        this.metadata.readFrom(buffer);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event){
        if(!this.enabled) return;

        double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_TRUE, GL11.GL_FALSE);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        double rx = this.metadata.spawnPoint.posX - dx;
        double ry = this.metadata.spawnPoint.posY - dy;
        double rz = this.metadata.spawnPoint.posZ - dz;
        this.drawBox(rx, ry, rz, 0x00FF00);
        this.renderLabel("World spawn", rx + 0.5, ry + 1.5, rz + 0.5);

        for(Spawnpoint spawnpoint : this.metadata.randomSpawnpoints){
            rx = spawnpoint.posX - dx;
            ry = spawnpoint.posY - dy;
            rz = spawnpoint.posZ - dz;
            this.drawBox(rx, ry, rz, 0x00FFFF);
            this.renderLabel(spawnpoint.name, rx + 0.5, ry + 1.5, rz + 0.5);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public void drawBox(double x, double y, double z, int color){
        float r = (float)(color >> 16 & 255) / 255;
        float g = (float)(color >> 8 & 255) / 255;
        float b = (float)(color & 255) / 255;

        mc.getTextureManager().bindTexture(noise);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator tess = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(r, g, b, 0.3f);
        tess.startDrawingQuads();
        tess.addVertex(0, 1, 0);
        tess.addVertex(1, 1, 0);
        tess.addVertex(1, 0, 0);
        tess.addVertex(0, 0, 0);

        tess.addVertex(0, 0, 1);
        tess.addVertex(1, 0, 1);
        tess.addVertex(1, 1, 1);
        tess.addVertex(0, 1, 1);

        tess.addVertex(0, 0, 0);
        tess.addVertex(1, 0, 0);
        tess.addVertex(1, 0, 1);
        tess.addVertex(0, 0, 1);

        tess.addVertex(0, 1, 1);
        tess.addVertex(1, 1, 1);
        tess.addVertex(1, 1, 0);
        tess.addVertex(0, 1, 0);

        tess.addVertex(0, 0, 1);
        tess.addVertex(0, 1, 1);
        tess.addVertex(0, 1, 0);
        tess.addVertex(0, 0, 0);

        tess.addVertex(1, 0, 0);
        tess.addVertex(1, 1, 0);
        tess.addVertex(1, 1, 1);
        tess.addVertex(1, 0, 1);
        tess.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(r, g, b, 1);
        tess.startDrawingQuads();
        tess.addVertexWithUV(0, 1, 0, 0, 1);
        tess.addVertexWithUV(1, 1, 0, 1, 1);
        tess.addVertexWithUV(1, 0, 0, 1, 0);
        tess.addVertexWithUV(0, 0, 0, 0, 0);

        tess.addVertexWithUV(0, 0, 1, 0, 1);
        tess.addVertexWithUV(1, 0, 1, 1, 1);
        tess.addVertexWithUV(1, 1, 1, 1, 0);
        tess.addVertexWithUV(0, 1, 1, 0, 0);

        tess.addVertexWithUV(0, 0, 0, 0, 1);
        tess.addVertexWithUV(1, 0, 0, 1, 1);
        tess.addVertexWithUV(1, 0, 1, 1, 0);
        tess.addVertexWithUV(0, 0, 1, 0, 0);

        tess.addVertexWithUV(0, 1, 1, 0, 1);
        tess.addVertexWithUV(1, 1, 1, 1, 1);
        tess.addVertexWithUV(1, 1, 0, 1, 0);
        tess.addVertexWithUV(0, 1, 0, 0, 0);

        tess.addVertexWithUV(0, 0, 1, 0, 1);
        tess.addVertexWithUV(0, 1, 1, 1, 1);
        tess.addVertexWithUV(0, 1, 0, 1, 0);
        tess.addVertexWithUV(0, 0, 0, 0, 0);

        tess.addVertexWithUV(1, 0, 0, 0, 1);
        tess.addVertexWithUV(1, 1, 0, 1, 1);
        tess.addVertexWithUV(1, 1, 1, 1, 0);
        tess.addVertexWithUV(1, 0, 1, 0, 0);
        tess.draw();

        GL11.glPopMatrix();
    }

    public void renderLabel(String label, double x, double y, double z){
        FontRenderer fontrenderer = mc.fontRenderer;
        RenderManager renderManager = RenderManager.instance;
        Tessellator tessellator = Tessellator.instance;

        int width = fontrenderer.getStringWidth(label) / 2;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.0266666688f, -0.0266666688f, 0.0266666688f);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-width - 1, -1, 0.01D);
        tessellator.addVertex(-width - 1, 8, 0.01D);
        tessellator.addVertex(width + 1, 8, 0.01D);
        tessellator.addVertex(width + 1, -1, 0.01D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        fontrenderer.drawString(label, -width, 0, -1);
        GL11.glPopMatrix();
    }

    public static MapEditManager instance(){
        return INSTANCE;
    }
}
