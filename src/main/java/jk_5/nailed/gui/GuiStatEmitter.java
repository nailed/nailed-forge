package jk_5.nailed.gui;

import codechicken.lib.data.MCDataInput;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiStatEmitter extends NailedGui {

    private TileEntityStatEmitter statEmitter;

    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;

    public GuiStatEmitter() {
        super(new ResourceLocation("nailed", "textures/gui/statemitter.png"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Stat Emitter", this.width / 2, 20, 0xFFFFFFFF);
        this.textField.drawTextBox();
    }

    @Override
    protected void readGuiData(MCDataInput input, World world) {
        TileEntity tile = world.getBlockTileEntity(input.readInt(), input.readInt(), input.readInt());
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) GuiHelper.doNotShow();
        this.statEmitter = (TileEntityStatEmitter) tile;
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
        this.textField.setText(this.statEmitter.getStatName());
        this.doneBtn.enabled = this.textField.getText().trim().length() > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled){
            if(button.id == 1){
                Minecraft.getMinecraft().displayGuiScreen(null);
            }else if(button.id == 0){
                Packets.STATEMITTER_STAT.newPacket().writeCoord(this.statEmitter.xCoord, this.statEmitter.yCoord, this.statEmitter.zCoord).writeString(this.textField.getText()).sendToServer();
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
