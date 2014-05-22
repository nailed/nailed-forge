package jk_5.nailed.client.map.edit;

import java.util.*;

import com.google.common.collect.*;

import org.lwjgl.opengl.*;

import io.netty.buffer.*;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.*;

import jk_5.nailed.client.gui.*;
import jk_5.nailed.map.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapEditManager {

    private static final MapEditManager INSTANCE = new MapEditManager();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final ResourceLocation noise = new ResourceLocation("nailed", "textures/effects/noise.png");

    private boolean enabled;
    @Getter private MapEditMetadata metadata = new MapEditMetadata();

    private Location facingSpawnpoint;
    private int infoWidth = 0;
    private ScaledResolution resolution;
    private List<String> lineBuffer = Lists.newArrayList();

    public void readData(ByteBuf buffer){
        this.metadata.readFrom(buffer);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(this.enabled && event.phase == TickEvent.Phase.START && mc.theWorld != null && mc.thePlayer != null){
            this.facingSpawnpoint = this.rayTraceSpawnpoints(mc.thePlayer, 200);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event){
        if(this.enabled && (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)){
            if(this.facingSpawnpoint != null){
                mc.displayGuiScreen(new GuiEditSpawnpoint(this.facingSpawnpoint));
            }
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event){
        this.resolution = event.resolution;
        if(!this.enabled || event.type != RenderGameOverlayEvent.ElementType.ALL || this.facingSpawnpoint == null){
            return;
        }
        this.lineBuffer.clear();

        //this.drawLine(ChatColor.GREEN + this.facingSpawnpoint.name);

        //this.doRenderLines();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event){
        if(!this.enabled){
            return;
        }

        double dx = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double dy = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double dz = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_TRUE, GL11.GL_FALSE);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        double rx = this.metadata.spawnPoint.getX() - dx;
        double ry = this.metadata.spawnPoint.getY() - dy;
        double rz = this.metadata.spawnPoint.getZ() - dz;
        this.drawSpawnpoint(rx, ry, rz, this.metadata.spawnPoint.getYaw(), this.facingSpawnpoint == this.metadata.spawnPoint ? 0xFF0000 : 0x00FF00);
        this.renderLabel("World spawn", rx, ry + 2, rz);

        for(Location spawnpoint : this.metadata.randomSpawnpoints){
            rx = spawnpoint.getX() - dx;
            ry = spawnpoint.getY() - dy;
            rz = spawnpoint.getZ() - dz;
            this.drawSpawnpoint(rx, ry, rz, spawnpoint.getYaw(), this.facingSpawnpoint == spawnpoint ? 0xFF0000 : 0x00FFFF);
            //this.renderLabel(spawnpoint.name, rx + 0.5, ry + 0.5, rz + 0.5);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public void drawSpawnpoint(double x, double y, double z, float yaw, int color){
        float r = (float) (color >> 16 & 255) / 255;
        float g = (float) (color >> 8 & 255) / 255;
        float b = (float) (color & 255) / 255;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(r, g, b, 1f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 1.5, 0);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glColor4f(r, g, b, 0.3f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(-0.2, 0, 0.2);
        GL11.glVertex3d(0.2, 0, 0.2);
        GL11.glVertex3d(0.2, 0, -0.2);
        GL11.glVertex3d(-0.2, 0, -0.2);
        GL11.glVertex3d(-0.2, 0, -0.2);
        GL11.glVertex3d(0.2, 0, -0.2);
        GL11.glVertex3d(0.2, 0, 0.2);
        GL11.glVertex3d(-0.2, 0, 0.2);
        GL11.glEnd();

        GL11.glColor4f(r, g, b, 1f);
        GL11.glRotatef(-yaw, 0, 1, 0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3d(0, 1.1, 0);
        GL11.glVertex3d(0, 1.3, 0.5);
        GL11.glVertex3d(0, 1.5, 0);
        GL11.glVertex3d(0, 1.5, 0);
        GL11.glVertex3d(0, 1.3, 0.5);
        GL11.glVertex3d(0, 1.1, 0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
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

    public Vec3 getCorrectedHeadVec(EntityPlayer player){
        Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        if(player.worldObj.isRemote){
            v.yCoord += player.getEyeHeight() - player.getDefaultEyeHeight();
        }else{
            v.yCoord += player.getEyeHeight();
            if(player instanceof EntityPlayerMP && player.isSneaking()){
                v.yCoord -= 0.08;
            }
        }
        return v;
    }

    public Location rayTraceSpawnpoints(EntityPlayer player, double reach){
        Vec3 startVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLookVec();
        Vec3 endVec = startVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        if(!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)){
            if(!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)){
                double endX = endVec.xCoord;
                double endY = endVec.yCoord;
                double endZ = endVec.zCoord;
                double startX = startVec.xCoord;
                double startY = startVec.yCoord;
                double startZ = startVec.zCoord;

                Location spawnpoint = this.metadata.getSpawnpoint(startX, startY, startZ);

                if(spawnpoint != null){
                    return spawnpoint;
                }

                int k1 = 200;

                while(k1-- >= 0){
                    if(Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)){
                        return null;
                    }

                    if(startX == endX && startY == endY && startZ == endZ){
                        return null;
                    }

                    boolean flag6 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if(endX > startX){
                        d0 = (double) startX + 1.0D;
                    }else if(endX < startX){
                        d0 = (double) startX + 0.0D;
                    }else{
                        flag6 = false;
                    }

                    if(endY > startY){
                        d1 = (double) startY + 1.0D;
                    }else if(endY < startY){
                        d1 = (double) startY + 0.0D;
                    }else{
                        flag3 = false;
                    }

                    if(endZ > startZ){
                        d2 = (double) startZ + 1.0D;
                    }else if(endZ < startZ){
                        d2 = (double) startZ + 0.0D;
                    }else{
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = endVec.xCoord - startVec.xCoord;
                    double d7 = endVec.yCoord - startVec.yCoord;
                    double d8 = endVec.zCoord - startVec.zCoord;

                    if(flag6){
                        d3 = (d0 - startVec.xCoord) / d6;
                    }

                    if(flag3){
                        d4 = (d1 - startVec.yCoord) / d7;
                    }

                    if(flag4){
                        d5 = (d2 - startVec.zCoord) / d8;
                    }

                    byte b0;

                    if(d3 < d4 && d3 < d5){
                        if(endX > startX){
                            b0 = 4;
                        }else{
                            b0 = 5;
                        }

                        startVec.xCoord = d0;
                        startVec.yCoord += d7 * d3;
                        startVec.zCoord += d8 * d3;
                    }else if(d4 < d5){
                        if(endY > startY){
                            b0 = 0;
                        }else{
                            b0 = 1;
                        }

                        startVec.xCoord += d6 * d4;
                        startVec.yCoord = d1;
                        startVec.zCoord += d8 * d4;
                    }else{
                        if(endZ > startZ){
                            b0 = 2;
                        }else{
                            b0 = 3;
                        }

                        startVec.xCoord += d6 * d5;
                        startVec.yCoord += d7 * d5;
                        startVec.zCoord = d2;
                    }

                    Vec3 vec32 = mc.theWorld.getWorldVec3Pool().getVecFromPool(startVec.xCoord, startVec.yCoord, startVec.zCoord);
                    startX = (int) (vec32.xCoord = (double) MathHelper.floor_double(startVec.xCoord));

                    if(b0 == 5){
                        --startX;
                        ++vec32.xCoord;
                    }

                    startY = (int) (vec32.yCoord = (double) MathHelper.floor_double(startVec.yCoord));

                    if(b0 == 1){
                        --startY;
                        ++vec32.yCoord;
                    }

                    startZ = (int) (vec32.zCoord = (double) MathHelper.floor_double(startVec.zCoord));

                    if(b0 == 3){
                        --startZ;
                        ++vec32.zCoord;
                    }

                    spawnpoint = this.metadata.getSpawnpoint(startX, startY + 1, startZ);

                    if(spawnpoint != null){
                        return spawnpoint;
                    }
                }
            }
        }
        return null;
    }

    public void drawLine(String text){
        this.lineBuffer.add(text);
        this.infoWidth = Math.max(this.infoWidth, mc.fontRenderer.getStringWidth(text) + 2);
    }

    private void doRenderLines(){
        int y = 0;
        int height = mc.fontRenderer.FONT_HEIGHT + 2;
        Gui.drawRect(resolution.getScaledWidth() - this.infoWidth - 2, 0, this.resolution.getScaledWidth(), height * this.lineBuffer.size() + 1, 0x88000000);
        for(String line : this.lineBuffer){
            mc.fontRenderer.drawString(line, this.resolution.getScaledWidth() - this.infoWidth + 1, y + 2, 0xFFFFFFFF);
            y += height;
        }
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public static MapEditManager instance(){
        return INSTANCE;
    }
}
