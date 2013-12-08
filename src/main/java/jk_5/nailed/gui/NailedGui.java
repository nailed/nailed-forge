package jk_5.nailed.gui;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.render.CCRenderState;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public abstract class NailedGui extends GuiScreen {

    private final ResourceLocation background;

    protected int xSize = 176;
    protected int ySize = 166;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        GL11.glColor4f(1, 1, 1, 1);
        CCRenderState.changeTexture(this.background);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        super.drawScreen(mouseX, mouseY, partialTick);
    }

    protected abstract void readGuiData(MCDataInput input, World world);

    public final NailedGui readData(MCDataInput input){
        this.readGuiData(input, Minecraft.getMinecraft().theWorld);
        return this;
    }
}
