package jk_5.nailed.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

/**
 * No description given
 *
 * @author jk-5
 */
public class FixedWidthFontRenderer {

    public static final int FONT_WIDTH = 6;
    public static final int FONT_HEIGHT = 9;
    private static final String ALLOWED_CHARS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»";
    private static final String BASE16 = "0123456789abcdef";

    public static final int[] colours = {0x191919, 0xcc4c4c, 0x57a64e, 0x7f664c, 0x253192, 0xb266e5, 0x4c99b2, 0x999999, 0x4c4c4c, 0xf2b2cc, 0x7fcc19, 0xdede6c, 0x99b2f2, 0xe57fd8, 0xf2b233, 0xf0f0f0};

    private static ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
    private static ResourceLocation background = new ResourceLocation("nailed", "textures/gui/terminal-white.png");

    private int[] charWidth;
    private TextureManager textureManager;
    private int fontDisplayLists;
    private IntBuffer buffer;

    public FixedWidthFontRenderer(){
        this.charWidth = new int[256];
        this.buffer = GLAllocation.createDirectIntBuffer(1024);
        BufferedImage bufferedimage;
        try{
            bufferedimage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(font).getInputStream());
        }catch(IOException ioexception){
            throw new RuntimeException(ioexception);
        }

        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] ai = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, ai, 0, i);
        for(int k = 0; k < 256; k++){
            int l = k % 16;
            int k1 = k / 16;
            int j2 = 7;

            while(j2 >= 0){
                int i3 = l * 8 + j2;
                boolean flag = true;
                for(int l3 = 0; (l3 < 8) && (flag); l3++){
                    int i4 = (k1 * 8 + l3) * i;
                    int k4 = ai[(i3 + i4)] & 0xFF;
                    if(k4 > 0){
                        flag = false;
                    }
                }

                if(!flag){
                    break;
                }
                j2--;
            }
            if(k == 32){
                j2 = 2;
            }
            this.charWidth[k] = (j2 + 2);
        }

        this.fontDisplayLists = GLAllocation.generateDisplayLists(274);
        Tessellator tessellator = Tessellator.instance;
        for(int i1 = 0; i1 < 256; i1++){
            int startSpace = (FONT_WIDTH - this.charWidth[i1]) / 2;
            GL11.glNewList(this.fontDisplayLists + i1, 4864);
            GL11.glTranslatef(startSpace, 0.0F, 0.0F);
            tessellator.startDrawingQuads();
            int l1 = i1 % 16 * 8;
            int k2 = i1 / 16 * 8;
            float f = 7.99F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            tessellator.addVertexWithUV(0.0D, 0.0F + f, 0.0D, l1 / 128.0F + f1, (k2 + f) / 128.0F + f2);
            tessellator.addVertexWithUV(0.0F + f, 0.0F + f, 0.0D, (l1 + f) / 128.0F + f1, (k2 + f) / 128.0F + f2);
            tessellator.addVertexWithUV(0.0F + f, 0.0D, 0.0D, (l1 + f) / 128.0F + f1, k2 / 128.0F + f2);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, l1 / 128.0F + f1, k2 / 128.0F + f2);
            tessellator.draw();
            GL11.glTranslatef(FONT_WIDTH - startSpace, 0.0F, 0.0F);
            GL11.glEndList();
        }

        for(int j1 = 0; j1 < 16; j1++){
            int colour = colours[j1];
            int r = colour >> 16 & 0xFF;
            int g = colour >> 8 & 0xFF;
            int b = colour & 0xFF;
            GL11.glNewList(this.fontDisplayLists + 256 + j1, 4864);
            GL11.glColor3f(r / 255.0F, g / 255.0F, b / 255.0F);
            GL11.glEndList();
        }

        GL11.glNewList(this.fontDisplayLists + 256 + 16 + 0, 4864);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, FONT_HEIGHT, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(FONT_WIDTH, FONT_HEIGHT, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(FONT_WIDTH, 0.0D, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glTranslatef(FONT_WIDTH, 0.0F, 0.0F);
        GL11.glEndList();

        GL11.glNewList(this.fontDisplayLists + 256 + 16 + 1, 4864);
        GL11.glTranslatef(FONT_WIDTH, 0.0F, 0.0F);
        GL11.glEndList();
    }

    public void drawString(String s, int x, int y, String colours, int marginSize){
        drawString(s, x, y, colours, marginSize, false);
    }

    public void drawString(String s, int x, int y, String colours, float marginSize, boolean forceBackground){
        if(s == null){
            return;
        }
        if(this.textureManager == null){
            this.textureManager = Minecraft.getMinecraft().getTextureManager();
        }

        boolean hasBackgrounds = colours.length() > s.length();
        if(hasBackgrounds){
            this.textureManager.bindTexture(background);

            int lastColour = -1;

            if(marginSize > 0.0F){
                float marginSquish = marginSize / FONT_WIDTH;

                int colour1 = BASE16.indexOf(colours.charAt(s.length()));
                if((colour1 > 0) || (forceBackground)){
                    GL11.glPushMatrix();
                    GL11.glScalef(marginSquish, 1.0F, 1.0F);
                    GL11.glTranslatef((x - marginSize) / marginSquish, y, 0.0F);
                    GL11.glCallList(this.fontDisplayLists + 256 + colour1);
                    GL11.glCallList(this.fontDisplayLists + 256 + 16);
                    GL11.glPopMatrix();
                }

                int colour2 = "0123456789abcdef".indexOf(colours.charAt(s.length() + s.length() - 1));
                if((colour2 > 0) || (forceBackground)){
                    GL11.glPushMatrix();
                    GL11.glScalef(marginSquish, 1.0F, 1.0F);
                    GL11.glTranslatef((x + s.length() * FONT_WIDTH) / marginSquish, y, 0.0F);
                    GL11.glCallList(this.fontDisplayLists + 256 + colour2);
                    GL11.glCallList(this.fontDisplayLists + 256 + 16);
                    GL11.glPopMatrix();
                }

            }

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0F);

            this.buffer.clear();
            for(int i = 0; i < s.length(); i++){
                int pos = s.length() + i;
                int colour = "0123456789abcdef".indexOf(colours.charAt(pos));
                if(colour != lastColour){
                    this.buffer.put(this.fontDisplayLists + 256 + colour);
                    if(this.buffer.remaining() == 0){
                        this.buffer.flip();
                        GL11.glCallLists(this.buffer);
                        this.buffer.clear();
                    }
                    lastColour = colour;
                }

                this.buffer.put(this.fontDisplayLists + 256 + 16 + ((forceBackground) || (colour > 0) ? 0 : 1));
                if(this.buffer.remaining() == 0){
                    this.buffer.flip();
                    GL11.glCallLists(this.buffer);
                    this.buffer.clear();
                }

            }

            this.buffer.flip();
            GL11.glCallLists(this.buffer);
            GL11.glPopMatrix();
        }

        this.textureManager.bindTexture(font);

        int lastColour = -1;
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        this.buffer.clear();

        for(int i = 0; i < s.length(); i++){
            int colour = "0123456789abcdef".indexOf(colours.charAt(i));
            if(colour != lastColour){
                this.buffer.put(this.fontDisplayLists + 256 + colour);
                if(this.buffer.remaining() == 0){
                    this.buffer.flip();
                    GL11.glCallLists(this.buffer);
                    this.buffer.clear();
                }
                lastColour = colour;
            }

            int j = ALLOWED_CHARS.indexOf(s.charAt(i));
            if((j < 0) || (j >= 192)){
                j = ALLOWED_CHARS.indexOf('?');
            }

            this.buffer.put(this.fontDisplayLists + j + 32);
            if(this.buffer.remaining() == 0){
                this.buffer.flip();
                GL11.glCallLists(this.buffer);
                this.buffer.clear();
            }

        }

        this.buffer.flip();
        GL11.glCallLists(this.buffer);
        GL11.glPopMatrix();
    }

    public int getStringWidth(String s){
        if(s == null){
            return 0;
        }
        return s.length() * FONT_WIDTH;
    }
}
