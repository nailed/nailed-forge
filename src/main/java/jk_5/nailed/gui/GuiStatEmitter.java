package jk_5.nailed.gui;

import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.gui.container.ContainerStatEmitter;
import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiStatEmitter extends GuiContainer {

    public GuiStatEmitter(TileEntityStatEmitter tile) {
        super(new ContainerStatEmitter(tile));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {

    }
}
