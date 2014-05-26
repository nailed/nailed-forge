package jk_5.nailed.client.render;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.*;
import jk_5.nailed.map.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.client.event.*;
import org.lwjgl.opengl.*;

import java.util.*;

/**
 * Created by matthias on 24-5-14.
 */
public class CustomsRenderer {
    private static final CustomsRenderer INSTANCE = new CustomsRenderer();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private List<RenderPoint[]> objects = Lists.newArrayList();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_TRUE, GL11.GL_FALSE);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        if(objects.size() == 0) return;
        for(RenderPoint[] points : objects){
            switch (points.length){
                case 0:
                    continue;
                case 1:
                    renderPoint(points, dx, dy, dz);
                    break;
                case 2:
                    renderLine(points, dx, dy, dz);
                    break;
                case 3:
                    renderSurface(points, dx, dy, dz);
                    break;
            }
        }
    }

    public void renderPoint(RenderPoint[] points, double dx, double dy, double dz) {

        RenderPoint point = points[0];

        double x = point.getX() + dx;
        double y = point.getY() + dy;
        double z = point.getZ() + dz;

        double distance = Math.sqrt(x*x + y*y + z*z);

        int color = point.getColor();

        float[] c = getColors(color);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glColor4f(c[0], c[1], c[2], c[3]);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth((float) (point.getSize() * distance));

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public void renderLine(RenderPoint[] points, double dx, double dy, double dz) {
        RenderPoint p1 = points[0];
        RenderPoint p2 = points[1];

        double x1 = p1.getX() + dx;
        double y1 = p1.getY() + dy;
        double z1 = p1.getZ() + dz;

        double x2 = p2.getX() + dx;
        double y2 = p2.getY() + dy;
        double z2 = p2.getZ() + dz;

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;
        double z = (z1 + z2) / 2;

        double distance = Math.sqrt(x*x + y*y + z*z);

        int c1 = p1.getColor();
        int c2 = p2.getColor();

        float[] c1a = getColors(c1);
        float[] c2a = getColors(c2);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(c1a[0], c1a[1], c1a[2], c1a[3]);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth((float) (p1.getSize() * distance));
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glColor4f(c2a[0], c2a[1], c2a[2], c2a[3]);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public void renderSurface(RenderPoint[] points, double dx, double dy, double dz) {
        RenderPoint p1 = points[0];
        RenderPoint p2 = points[1];
        RenderPoint p3 = points[3];

        float[] c1 = getColors(p1.getColor());
        float[] c2 = getColors(p2.getColor());
        float[] c3 = getColors(p3.getColor());

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(c1[0], c1[1], c1[2], c1[3]);

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3d(p1.getX() + dx, p1.getY() + dy, p1.getZ() + dz);
        GL11.glColor4f(c2[0], c2[1], c2[2], c2[3]);
        GL11.glVertex3d(p2.getX() + dx, p2.getY() + dy, p2.getZ() + dz);
        GL11.glColor4f(c3[0], c3[1], c3[2], c3[3]);
        GL11.glVertex3d(p3.getX() + dx, p3.getY() + dy, p3.getZ() + dz);
        GL11.glVertex3d(p3.getX() + dx, p3.getY() + dy, p3.getZ() + dz);
        GL11.glColor4f(c2[0], c2[1], c2[2], c2[3]);
        GL11.glVertex3d(p2.getX() + dx, p2.getY() + dy, p2.getZ() + dz);
        GL11.glColor4f(c1[0], c1[1], c1[2], c1[3]);
        GL11.glVertex3d(p1.getX() + dx, p1.getY() + dy, p1.getZ() + dz);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
    }

    public void setRenderList(List<RenderPoint[]> renderList) { this.objects = renderList; }

    public void addRenderList(List<RenderPoint[]> renderList) { this.objects.addAll(renderList); }

    public void clearRenderList(){ this.objects.clear(); }

    private float[] getColors(int colors){
        float[] cl = new float[4];
        cl[0] = (float) (colors >> 24 & 255) / 255;
        cl[1] = (float) (colors >> 26 & 255) / 255;
        cl[2] = (float) (colors >> 8 & 255) / 255;
        cl[3] = (float) (colors & 255) / 255;
        return cl;
    }

    public static CustomsRenderer instance() { return INSTANCE; }
}
