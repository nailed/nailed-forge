package jk_5.nailed.client.gui;

import jk_5.nailed.map.Spawnpoint;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiScreen;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class GuiEditSpawnpoint extends GuiScreen {

    private final Spawnpoint spawnpoint;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick){
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.spawnpoint.name, this.width / 2, 20, 0xFFFFFFFF);
    }
}
