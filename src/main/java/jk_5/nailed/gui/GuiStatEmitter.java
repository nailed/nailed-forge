package jk_5.nailed.gui;

import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.map.stat.StatMode;
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
public class GuiStatEmitter extends NailedGui {

    private final TileEntityStatEmitter statEmitter;

    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton modeBtn;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Stat Emitter", this.width / 2, 20, 0xFFFFFFFF);
        this.textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(this.modeBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72 + 12, "Mode: " + this.statEmitter.getMode().name().toLowerCase()));
        this.buttonList.add(this.doneBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.getString("gui.done")));
        this.buttonList.add(this.cancelBtn = new GuiButton(2, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.getString("gui.cancel")));
        this.textField = new GuiTextField(this.fontRenderer, this.width / 2 - 150, 60, 300, 20);
        this.textField.setMaxStringLength(32767);
        this.textField.setFocused(true);
        this.textField.setText(this.statEmitter.getProgrammedName());
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled){
            if(button.id == 2){
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 1){
                Packets.STATEMITTER_STAT.newPacket().writeCoord(this.statEmitter.xCoord, this.statEmitter.yCoord, this.statEmitter.zCoord)
                        .writeByte(this.statEmitter.getMode().ordinal())
                        .writeString(this.textField.getText())
                        .sendToServer();
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 0){
                int next = this.statEmitter.getMode().ordinal() + 1;
                if(next == StatMode.values().length){
                    next = 0;
                }
                this.statEmitter.setMode(StatMode.values()[next]);
                this.modeBtn.displayString = "Mode: " + this.statEmitter.getMode().name().toLowerCase();
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
