package jk_5.nailed.client.gui;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import net.minecraft.client.gui.*;
import net.minecraft.util.*;

import jk_5.nailed.client.gui.elements.*;
import jk_5.nailed.client.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiTerminal extends GuiScreen {

    private static final ResourceLocation background = new ResourceLocation("nailed", "textures/gui/terminal.png");
    private ClientMachine machine;
    private ElementTerminal terminalBox = null;
    private int terminalWidth;
    private int terminalHeight;

    public GuiTerminal(ClientMachine machine, int termWidth, int termHeight){
        this.machine = machine;
        this.terminalWidth = termWidth;
        this.terminalHeight = termHeight;
    }

    @Override
    public void initGui(){
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.terminalBox = new ElementTerminal(0, 0, this.terminalWidth, this.terminalHeight, this.machine);
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
        int startX = (this.width - this.terminalBox.getWidth()) / 2;
        int startY = (this.height - this.terminalBox.getHeight()) / 2;
        this.terminalBox.mouseClicked(x - startX, y - startY, button);
    }

    public void handleMouseInput(){
        super.handleMouseInput();

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int startX = (this.width - this.terminalBox.getWidth()) / 2;
        int startY = (this.height - this.terminalBox.getHeight()) / 2;
        this.terminalBox.handleMouseInput(x - startX, y - startY);
    }

    public void drawScreen(int mouseX, int mouseY, float f){
        int startX = (this.width - this.terminalBox.getWidth()) / 2;
        int startY = (this.height - this.terminalBox.getHeight()) / 2;
        int endX = startX + this.terminalBox.getWidth();
        int endY = startY + this.terminalBox.getHeight();

        drawDefaultBackground();

        this.terminalBox.draw(this.mc, startX, startY);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);

        this.drawTexturedModalRect(startX - 12, startY - 12, 12, 28, 12, 12);
        this.drawTexturedModalRect(startX - 12, endY, 12, 40, 12, 16);
        this.drawTexturedModalRect(endX, startY - 12, 24, 28, 12, 12);
        this.drawTexturedModalRect(endX, endY, 24, 40, 12, 16);

        this.drawTexturedModalRect(startX, startY - 12, 0, 0, endX - startX, 12);
        this.drawTexturedModalRect(startX, endY, 0, 12, endX - startX, 16);

        this.drawTexturedModalRect(startX - 12, startY, 0, 28, 12, endY - startY);
        this.drawTexturedModalRect(endX, startY, 36, 28, 12, endY - startY);
    }
}
