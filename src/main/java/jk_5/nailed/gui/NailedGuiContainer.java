package jk_5.nailed.gui;

import codechicken.lib.render.CCRenderState;
import jk_5.nailed.gui.container.NailedContainer;
import lombok.Getter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedGuiContainer extends GuiContainer {

    @Getter
    private final NailedContainer container;
    private final ResourceLocation backgroundTexture;

    public NailedGuiContainer(NailedContainer container, ResourceLocation background) {
        super(container);
        this.container = container;
        this.backgroundTexture = background;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        CCRenderState.changeTexture(this.backgroundTexture);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
