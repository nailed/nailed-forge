package jk_5.nailed.gui;

import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import jk_5.nailed.network.Packets;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class GuiPortalController extends NailedGui {

    private final TileEntityPortalController portalController;

    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Portal Controller", this.width / 2, 20, 0xFFFFFFFF);
        this.textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("gui.done")));
        this.buttonList.add(this.cancelBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
        this.textField = new GuiTextField(this.fontRenderer, this.width / 2 - 150, 60, 300, 20);
        this.textField.setMaxStringLength(32767);
        this.textField.setFocused(true);
        this.textField.setText(this.portalController.getProgrammedName());
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled){
            if(button.id == 1){
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 0){
                Packets.PORTALCONTROLLER_DESTINATION.newPacket().writeCoord(this.portalController.xCoord, this.portalController.yCoord, this.portalController.zCoord)
                        .writeString(this.textField.getText())
                        .sendToServer();
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        this.textField.textboxKeyTyped(c, i);
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;

        if(i == Keyboard.KEY_ESCAPE){
            this.actionPerformed(this.cancelBtn);
        }else if(i == Keyboard.KEY_RETURN){
            this.actionPerformed(this.doneBtn);
        }
    }

    @Override
    protected void mouseClicked(int i, int i2, int i3) {
        super.mouseClicked(i, i2, i3);
        this.textField.mouseClicked(i, i2, i3);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.textField.updateCursorCounter();
    }
}
