package jk_5.nailed.client.config;

import cpw.mods.fml.client.FMLClientHandler;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedConfigGuiScreen extends GuiScreen {

    private final GuiScreen parent;

    @SuppressWarnings("unchecked")
    @Override
    public void initGui(){
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton){
        if(par1GuiButton.enabled && par1GuiButton.id == 1){
            FMLClientHandler.instance().showGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Nailed config screen (Without config)", this.width / 2, 40, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }
}
