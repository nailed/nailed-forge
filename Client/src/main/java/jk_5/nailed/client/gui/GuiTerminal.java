package jk_5.nailed.client.gui;

import jk_5.nailed.client.gui.elements.ElementTerminal;
import jk_5.nailed.client.scripting.MachineSynchronizer;
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
public class GuiTerminal extends GuiScreen {

    private static final ResourceLocation background = new ResourceLocation("nailed", "textures/gui/terminal.png");
    private MachineSynchronizer terminal;
    private ElementTerminal terminalBox = null;
    private int xSize = 176;
    private int ySize = 166;

    @Override
    public void initGui(){
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        this.terminalBox = new ElementTerminal(1, this.width, this.height);
        this.buttonList.add(this.terminalBox);

        this.xSize = (this.terminalBox.getButtonWidth() + 24);
        this.ySize = (this.terminalBox.getButtonHeight() + 24);
    }

    @Override
    public void onGuiClosed(){
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    public void updateScreen(){
        super.updateScreen();
        this.terminalBox.update();
    }

    @Override
    protected void keyTyped(char c, int k){
        if(k == 1){
            super.keyTyped(c, k);
        }else{
            this.terminalBox.keyTyped(c, k);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button){
        this.terminalBox.mouseClicked(x, y, button);
    }

    public void handleMouseInput(){
        super.handleMouseInput();

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        this.terminalBox.handleMouseInput(x, y);
    }

    public void drawScreen(int mouseX, int mouseY, float f){
        this.drawDefaultBackground();

        this.terminalBox.drawButton(this.mc, mouseX, mouseY);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);

        int startX = this.terminalBox.xPosition;
        int startY = this.terminalBox.yPosition;
        int endX = startX + this.terminalBox.getButtonWidth();
        int endY = startY + this.terminalBox.getButtonHeight();

        this.drawTexturedModalRect(startX - 12, startY - 12, 12, 28, 12, 12);
        this.drawTexturedModalRect(startX - 12, endY, 12, 40, 12, 16);
        this.drawTexturedModalRect(endX, startY - 12, 24, 28, 12, 12);
        this.drawTexturedModalRect(endX, endY, 24, 40, 12, 16);

        this.drawTexturedModalRect(startX, startY - 12, 0, 0, this.terminalBox.getButtonWidth(), 12);
        this.drawTexturedModalRect(startX, endY, 0, 12, this.terminalBox.getButtonWidth(), 16);

        this.drawTexturedModalRect(startX - 12, startY, 0, 28, 12, this.terminalBox.getButtonHeight());
        this.drawTexturedModalRect(endX, startY, 36, 28, 12, this.terminalBox.getButtonHeight());
    }
}
