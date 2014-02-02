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
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 75, this.field_146295_m - 38, I18n.getStringParams("gui.done")));
    }

    @Override
    protected void func_146284_a(GuiButton par1GuiButton){
        if (par1GuiButton.field_146124_l && par1GuiButton.field_146127_k == 1){
            FMLClientHandler.instance().showGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3){
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, "Nailed config screen (Without config)", this.field_146294_l / 2, 40, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }
}
