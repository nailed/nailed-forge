package jk_5.nailed.gui;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public abstract class NailedGui extends GuiScreen {

    //private final ResourceLocation background;

    protected int xSize = 176;
    protected int ySize = 166;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        /*GL11.glColor4f(1, 1, 1, 1);
        CCRenderState.changeTexture(this.background);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);*/

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    public NailedGui readData(ByteBuf buffer){
        return this;
    }
}
