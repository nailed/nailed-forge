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
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Portal Controller", this.width / 2, 20, 0xFFFFFFFF);
        this.textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.addButton(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("gui.done")));
        this.addButton(this.cancelBtn = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.textField = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 60, 300, 20);
        this.textField.setMaxStringLength(32767);
        this.textField.setFocused(true);
        this.textField.setText(this.mapName);
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled){
            if(button.id == 1){
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 0){
                this.sendGuiData();
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

    @Override
    public NailedGui readGuiData(ByteBuf buffer){
        this.mapName = ByteBufUtils.readUTF8String(buffer);
        return this;
    }

    @Override
    protected void writeGuiData(ByteBuf buffer){
        ByteBufUtils.writeUTF8String(buffer, this.textField.getText());
    }
}
