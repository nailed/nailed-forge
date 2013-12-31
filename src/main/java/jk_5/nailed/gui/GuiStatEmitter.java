package jk_5.nailed.gui;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.map.stat.StatMode;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
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
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, "Stat Emitter", this.field_146294_l / 2, 20, 0xFFFFFFFF);
        this.textField.func_146194_f();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.field_146292_n.clear();
        this.field_146292_n.add(this.modeBtn = new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 72 + 12, "Mode: " + this.statEmitter.getMode().name().toLowerCase()));
        this.field_146292_n.add(this.doneBtn = new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 96 + 12, I18n.getStringParams("gui.done")));
        this.field_146292_n.add(this.cancelBtn = new GuiButton(2, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 120 + 12, I18n.getStringParams("gui.cancel")));
        this.textField = new GuiTextField(this.field_146289_q, this.field_146294_l / 2 - 150, 60, 300, 20);
        this.textField.func_146203_f(32767);
        this.textField.func_146195_b(true);
        this.textField.func_146180_a(this.statEmitter.getProgrammedName());
        this.doneBtn.field_146125_m = this.textField.func_146179_b().trim().length() > 0;
    }

    @Override
    protected void func_146284_a(GuiButton button) {
        if(button.field_146125_m){
            if(button.field_146127_k == 2){
                Minecraft.getMinecraft().func_147108_a(null);
            }else if(button.field_146127_k == 1){
                ByteBuf data = Unpooled.buffer();
                data.writeByte(this.statEmitter.getMode().ordinal());
                ByteBufUtils.writeUTF8String(data, this.textField.func_146179_b());
                NailedNetworkHandler.sendPacketToServer(new NailedPacket.GuiReturnDataPacket(this.statEmitter.field_145851_c, this.statEmitter.field_145848_d, this.statEmitter.field_145849_e, data));

                Minecraft.getMinecraft().func_147108_a(null);
            }else if(button.field_146127_k == 0){
                int next = this.statEmitter.getMode().ordinal() + 1;
                if(next == StatMode.values().length){
                    next = 0;
                }
                this.statEmitter.setMode(StatMode.values()[next]);
                this.modeBtn.field_146126_j = "Mode: " + this.statEmitter.getMode().name().toLowerCase();
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
