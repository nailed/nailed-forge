package jk_5.nailed.client.gui;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.client.blocks.tileentity.IGuiTileEntity;
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
public class GuiPortalController extends NailedGui {

    private String mapName;

    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;

    public GuiPortalController(IGuiTileEntity tileEntity){
        super(tileEntity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, "Portal Controller", this.field_146294_l / 2, 20, 0xFFFFFFFF);
        this.textField.func_146194_f();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.addButton(this.doneBtn = new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("gui.done")));
        this.addButton(this.cancelBtn = new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel")));
        this.textField = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 150, 60, 300, 20);
        this.textField.func_146203_f(32767);
        this.textField.func_146195_b(true);
        this.textField.func_146180_a(this.mapName);
        this.doneBtn.field_146125_m = this.textField.func_146179_b().trim().length() > 0;
    }

    @Override
    protected void func_146284_a(GuiButton button) {
        if(button.field_146125_m){
            if(button.field_146127_k == 1){
                Minecraft.getMinecraft().func_147108_a(null);
            }else if(button.field_146127_k == 0){
                this.sendGuiData();
                Minecraft.getMinecraft().func_147108_a(null);
            }
        }
    }

    @Override
    protected void keyTyped(char c, int i) {
        this.textField.func_146201_a(c, i);
        //doneBtn.enabled  \/
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
        this.textField.func_146178_a(); //updateCursorCounter
    }

    @Override
    public NailedGui readGuiData(ByteBuf buffer){
        this.mapName = ByteBufUtils.readUTF8String(buffer);
        return this;
    }

    @Override
    protected void writeGuiData(ByteBuf buffer){
        ByteBufUtils.writeUTF8String(buffer, this.textField.func_146179_b());
    }
}
