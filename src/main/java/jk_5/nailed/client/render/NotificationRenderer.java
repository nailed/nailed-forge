package jk_5.nailed.client.render;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.coremod.NailedModContainer;
import jk_5.nailed.util.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NotificationRenderer {

    public static List<Notification> notifications = Lists.newArrayList();
    private static final int notificationDelay = NailedModContainer.config.getTag("notification").getTag("delay").setComment("Delay before an notification will disappear").getIntValue(5000);
    private static final int notificationMax = NailedModContainer.config.getTag("notification").getTag("maxNotifications").setComment("Maximum number of notifications").getIntValue(5000);

    public static void addNotification(String text){
        addNotification(text, null, 0xFFFFFF);
    }

    public static void addNotification(String text, ResourceLocation image){
        addNotification(text, image, 0xFFFFFF);
    }

    public static void addNotification(String text, ResourceLocation image, int color){
        long time = System.nanoTime() / 1000000L;
        long timeBonus = notifications.size() == 0 ? notificationDelay / 2 : 0;
        notifications.add(new Notification(text, image, time + notificationDelay + timeBonus, time + notificationDelay / 4, color));
    }

    public static List<Notification> getListAndUpdate(long time){
        List<Notification> ret = Lists.newArrayList();
        boolean first = true;
        for(Notification notification : notifications){
            if(notification.expire >= time){
                if(first) ret.add(notification);
                else ret.add(new Notification(notification.text, notification.image, time + notificationDelay, notification.created, notification.color));
            }
            first = false;
        }
        notifications = ret;
        return ret;
    }

    @ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void render(RenderGameOverlayEvent.Post event){
        Minecraft mc = Minecraft.getMinecraft();
        long time = System.nanoTime() / 1000000L;
        if(event.type == RenderGameOverlayEvent.ElementType.ALL){
            if(getListAndUpdate(time).size() > 0){
                renderNotifications(event.resolution.getScaledWidth_double(), event.resolution.getScaledHeight_double(), time, event.partialTicks);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderNotifications(double sw, double sh, long time, float ptick){
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        int k = (int)sw;
        int l = (int)sh;

        List<Notification> notifications = getListAndUpdate(time);
        float shift = -8.0F;
        for (int entry = 0; entry < notifications.size() && entry < notificationMax; entry++) {
            Notification li = notifications.get(entry);

            String text = li.text;
            int size = mc.fontRenderer.getStringWidth(text) / 2;
            int alpha = 255;
            if ((entry == notifications.size() - 1) && (li.created > time)) {
                alpha = 255 - (int)((float)(li.created - time) / (notificationDelay / 4) * 240.0F);
            }
            if (li.expire < time + notificationDelay) {
                alpha = (int)(255.0F - (float)(time + notificationDelay - li.expire) / notificationDelay * 240.0F);
                shift = -8.0F * (alpha / 255.0F);
            }
            int color = (alpha / 2 << 24) + 16711680 + 65280 + 255;
            GL11.glPushMatrix();
            GL11.glTranslatef(k - size - 10, l - entry * 8 + shift, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            mc.ingameGUI.drawString(mc.fontRenderer, text, -4, -8, color);
            GL11.glPopMatrix();

            if (li.image != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef(k - 9, l - entry * 8 + shift - 6.0F, 0.0F);
                GL11.glScalef(0.03125F, 0.03125F, 0.03125F);
                mc.getTextureManager().bindTexture(li.image);
                Tessellator tessellator = Tessellator.instance;
                Color c = new Color(li.color);
                GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, alpha / 511.0F);
                RenderUtils.drawTexturedQuad(0, 0, 0, 0, 256, 256, -90.0D);
                GL11.glPopMatrix();
            }

            if ((entry == notifications.size() - 1) && (li.created > time)) {
                float scale = (float)(li.created - time) / (notificationDelay / 4);
                alpha = 255 - (int)(scale * 240.0F);
                GL11.glPushMatrix();
                GL11.glTranslatef(k - 5 - 8.0F * scale - (1.0F - scale) * (1.0F - scale) * (1.0F - scale) * size * 3.0F, l - entry * 8 + shift - 2.0F - 8.0F * scale, 0.0F);
                GL11.glScalef(scale / 2.0F, scale / 2.0F, scale / 2.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F - alpha / 511.0F);
                RenderUtils.bindTexture("textures/misc/particles.png");
                int px = 32 * ((mc.thePlayer.ticksExisted + entry * 3) % 16);
                int py = 32 * ((mc.thePlayer.ticksExisted + entry * 3) % 32 / 16);
                RenderUtils.drawTexturedQuad(0, 0, px, 96 + py, 32, 32, -90 - notifications.size());
                GL11.glPopMatrix();
            }
        }

        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }
}
