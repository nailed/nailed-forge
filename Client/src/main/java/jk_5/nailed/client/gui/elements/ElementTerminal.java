package jk_5.nailed.client.gui.elements;

import jk_5.nailed.client.NailedClient;
import jk_5.nailed.client.TickHandlerClient;
import jk_5.nailed.client.gui.ScriptingManager;
import jk_5.nailed.client.render.FixedWidthFontRenderer;
import jk_5.nailed.client.scripting.MachineSynchronizer;
import jk_5.nailed.map.script.Terminal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * No description given
 *
 * @author jk-5
 */
public class ElementTerminal extends GuiButton {

    private static final ResourceLocation background = new ResourceLocation("nailed", "textures/gui/terminal-bg.png");
    private static int MARGIN = 2;

    private MachineSynchronizer synchronizer;
    private float terminateTimer;
    private float rebootTimer;
    private float shutdownTimer;
    private int lastClickButton;
    private int lastClickX;
    private int lastClickY;

    public ElementTerminal(int id, int screenWidth, int screenHeight){
        super(id, 0, 0, screenWidth, screenHeight, "");
        this.commonInit();
        this.xPosition = ((screenWidth - this.width) / 2);
        this.yPosition = ((screenHeight - this.height) / 2);
    }

    public ElementTerminal(int id, int x, int y, int screenWidth, int screenHeight){
        super(id, 0, 0, screenWidth, screenHeight, "");
        this.commonInit();
        this.xPosition = x;
        this.yPosition = y;
    }

    private void commonInit(){
        ScriptingManager.currentSynchronizer = new MachineSynchronizer();
        Terminal term = ScriptingManager.currentSynchronizer.getTerminal();
        this.width = 2 * MARGIN + term.getWidth() * FixedWidthFontRenderer.FONT_WIDTH;
        this.height = 2 * MARGIN + term.getHeight() * FixedWidthFontRenderer.FONT_HEIGHT;
        this.visible = false;

        this.synchronizer = ScriptingManager.currentSynchronizer;
        this.terminateTimer = 0.0F;
        this.rebootTimer = 0.0F;
        this.shutdownTimer = 0.0F;

        this.lastClickButton = -1;
        this.lastClickX = -1;
        this.lastClickY = -1;
    }

    public void keyTyped(char c, int k){
        if(c == '\026'){
            String clipboard = GuiScreen.getClipboardString();
            if(clipboard != null){
                int nl = clipboard.indexOf('\n');
                if(nl == 0)
                    clipboard = "";
                else if(nl >= 0){
                    clipboard = clipboard.substring(0, nl - 1);
                }
                if(clipboard.length() > 128){
                    clipboard = clipboard.substring(0, 127);
                }
                this.synchronizer.typeString(clipboard);
            }
            return;
        }

        if((this.terminateTimer < 0.5F) && (this.rebootTimer < 0.5F) && (this.shutdownTimer < 0.5F)){
            if(this.synchronizer != null){
                this.synchronizer.pressKey(c, k);
            }
        }
    }

    public void mouseClicked(int x, int y, int button){
        if((x >= this.xPosition) && (x < this.xPosition + this.width) && (y >= this.yPosition) && (y < this.yPosition + this.height) && (button >= 0) && (button <= 2)){
            Terminal term = this.synchronizer.getTerminal();
            int charX = (x - (this.xPosition + MARGIN)) / FixedWidthFontRenderer.FONT_WIDTH;
            int charY = (y - (this.yPosition + MARGIN)) / FixedWidthFontRenderer.FONT_HEIGHT;
            charX = Math.min(Math.max(charX, 0), term.getWidth() - 1);
            charY = Math.min(Math.max(charY, 0), term.getHeight() - 1);

            this.synchronizer.clickMouse(charX, charY, button);

            this.lastClickButton = button;
            this.lastClickX = charX;
            this.lastClickY = charY;
        }
    }

    public void handleMouseInput(int x, int y){
        if((this.lastClickButton >= 0) && (!Mouse.isButtonDown(this.lastClickButton))){
            this.lastClickButton = -1;
        }

        int wheelChange = Mouse.getEventDWheel();
        if((wheelChange == 0) && (this.lastClickButton == -1)){
            return;
        }

        if((x >= this.xPosition) && (x < this.xPosition + this.width) && (y >= this.yPosition) && (y < this.yPosition + this.height)){
            Terminal term = this.synchronizer.getTerminal();
            int charX = (x - (this.xPosition + MARGIN)) / FixedWidthFontRenderer.FONT_WIDTH;
            int charY = (y - (this.yPosition + MARGIN)) / FixedWidthFontRenderer.FONT_HEIGHT;
            charX = Math.min(Math.max(charX, 0), term.getWidth() - 1);
            charY = Math.min(Math.max(charY, 0), term.getHeight() - 1);

            if(wheelChange < 0){
                this.synchronizer.clickMouse(charX, charY, 3);
            }else if(wheelChange > 0){
                this.synchronizer.clickMouse(charX, charY, 4);
            }

            if((this.lastClickButton >= 0) && ((charX != this.lastClickX) || (charY != this.lastClickY))){
                this.synchronizer.clickMouse(charX, charY, this.lastClickButton + 5);
                this.lastClickX = charX;
                this.lastClickY = charY;
            }
        }
    }

    public void update(){
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
            if(Keyboard.isKeyDown(Keyboard.KEY_C)){
                if(this.terminateTimer < 1.0F){
                    this.terminateTimer += 0.05F;
                    if(this.terminateTimer >= 1.0F){
                        if(this.synchronizer != null){
                            this.synchronizer.terminate();
                        }
                    }
                }
            }else{
                this.terminateTimer = 0.0F;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_R)){
                if(this.rebootTimer < 1.0F){
                    this.rebootTimer += 0.05F;
                    if(this.rebootTimer >= 1.0F){
                        if(this.synchronizer != null){
                            this.synchronizer.reboot();
                        }
                    }
                }
            }else{
                this.rebootTimer = 0.0F;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                if(this.shutdownTimer < 1.0F){
                    this.shutdownTimer += 0.05F;
                    if(this.shutdownTimer >= 1.0F){
                        if(this.synchronizer != null){
                            this.synchronizer.shutdown();
                        }
                    }
                }
            }else{
                this.shutdownTimer = 0.0F;
            }
        }else{
            this.terminateTimer = 0.0F;
            this.rebootTimer = 0.0F;
            this.shutdownTimer = 0.0F;
        }
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY){
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int startX = this.xPosition;
        int startY = this.yPosition;
        int endX = startX + this.width;
        int endY = startY + this.height;

        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(startX, startY, 0, 0, this.width, this.height);

        Terminal terminal = this.synchronizer != null ? this.synchronizer.getTerminal() : null;
        if(terminal != null){
            synchronized(terminal){
                FixedWidthFontRenderer fontRenderer = NailedClient.getFixedWidthFontRenderer();
                boolean tblink = (terminal.isCursorBlink()) && TickHandlerClient.blinkOn();
                int tw = terminal.getWidth();
                int th = terminal.getHeight();
                int tx = terminal.getCursorX();
                int ty = terminal.getCursorY();

                int x = startX + MARGIN;
                int y = startY + MARGIN;

                String emptyLine = terminal.getLine(-1);
                fontRenderer.drawString(emptyLine, x, y - FixedWidthFontRenderer.FONT_HEIGHT, terminal.getColorLine(0), MARGIN);
                fontRenderer.drawString(emptyLine, x, y + th * FixedWidthFontRenderer.FONT_HEIGHT, terminal.getColorLine(th - 1), MARGIN);

                for(int line = 0; line < th; line++){
                    String text = terminal.getLine(line);
                    String colour = terminal.getColorLine(line);
                    fontRenderer.drawString(text, x, y, colour, MARGIN);
                    if(tblink && ty == line && tx >= 0 && tx < tw){
                        String cursorColour = "0123456789abcdef".charAt(terminal.getTextColor()) + "";
                        fontRenderer.drawString("_", x + FixedWidthFontRenderer.FONT_WIDTH * tx, y, cursorColour, 0);
                    }

                    y += FixedWidthFontRenderer.FONT_HEIGHT;
                }
            }
        }
    }

    public int getButtonHeight(){
        return this.height;
    }
}
