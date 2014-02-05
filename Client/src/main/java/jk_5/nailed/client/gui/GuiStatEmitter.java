package jk_5.nailed.client.gui;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.client.blocks.tileentity.IGuiTileEntity;
import jk_5.nailed.client.util.StatMode;
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
public class GuiStatEmitter extends NailedGui {

    private String statName;
    private StatMode mode;
    private int pulseLength;

    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton modeBtn;

    public GuiStatEmitter(IGuiTileEntity tileEntity){
        super(tileEntity);
    }

    @Override
    public NailedGui readGuiData(ByteBuf buffer){
        this.statName = ByteBufUtils.readUTF8String(buffer);
        this.mode = StatMode.values()[buffer.readByte()];
        this.pulseLength = buffer.readByte();
        return this;
    }

    @Override
    protected void writeGuiData(ByteBuf buffer){
        ByteBufUtils.writeUTF8String(buffer, this.textField.getText());
        buffer.writeByte(this.mode.ordinal());
        buffer.writeByte(this.pulseLength);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Stat Emitter", this.width / 2, 20, 0xFFFFFFFF);
        this.textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.addButton(this.modeBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72 + 12, "Mode: " + this.mode.name().toLowerCase()));
        this.addButton(this.doneBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("gui.done")));
        this.addButton(this.cancelBtn = new GuiButton(2, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.textField = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 60, 300, 20);
        this.textField.setMaxStringLength(32767);
        this.textField.setFocused(true);
        this.textField.setText(this.statName);
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled){
            if(button.id == 2){
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 1){
                this.sendGuiData();
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 0){
                int next = this.mode.ordinal() + 1;
                if(next == StatMode.values().length){
                    next = 0;
                }
                this.mode = StatMode.values()[next];
                this.modeBtn.displayString = "Mode: " + this.mode.name().toLowerCase();
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
