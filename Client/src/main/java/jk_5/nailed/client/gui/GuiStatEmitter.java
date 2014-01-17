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
        ByteBufUtils.writeUTF8String(buffer, this.textField.func_146179_b());
        buffer.writeByte(this.mode.ordinal());
        buffer.writeByte(this.pulseLength);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, "Stat Emitter", this.field_146294_l / 2, 20, 0xFFFFFFFF);
        this.textField.func_146194_f();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.addButton(this.modeBtn = new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72 + 12, "Mode: " + this.mode.name().toLowerCase()));
        this.addButton(this.doneBtn = new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("gui.done")));
        this.addButton(this.cancelBtn = new GuiButton(2, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel")));
        this.textField = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 150, 60, 300, 20);
        this.textField.func_146203_f(32767);
        this.textField.func_146195_b(true);
        this.textField.func_146180_a(this.statName);
        this.doneBtn.field_146125_m = this.textField.func_146179_b().trim().length() > 0;
    }

    @Override
    protected void func_146284_a(GuiButton button) {
        if(button.field_146125_m){
            if(button.field_146127_k == 2){
                Minecraft.getMinecraft().func_147108_a(null);
            }else if(button.field_146127_k == 1){
                this.sendGuiData();
                Minecraft.getMinecraft().func_147108_a(null);
            }else if(button.field_146127_k == 0){
                int next = this.mode.ordinal() + 1;
                if(next == StatMode.values().length){
                    next = 0;
                }
                this.mode = StatMode.values()[next];
                this.modeBtn.field_146126_j = "Mode: " + this.mode.name().toLowerCase();
            }
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        this.textField.func_146201_a(c, i);
        this.doneBtn.field_146125_m = this.textField.func_146179_b().trim().length() > 0;

        if(i == Keyboard.KEY_ESCAPE){
            this.func_146284_a(this.cancelBtn);
        }else if(i == Keyboard.KEY_RETURN){
            this.func_146284_a(this.doneBtn);
        }
    }

    @Override
    protected void mouseClicked(int i, int i2, int i3) {
        super.mouseClicked(i, i2, i3);
        this.textField.func_146192_a(i, i2, i3);
    }

    @Override
    public void func_146281_b() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.textField.func_146178_a();
    }
}
