package jk_5.nailed.client.config;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedConfigGuiFactory implements IModGuiFactory {

    private Minecraft mc;
    private static final Set<RuntimeOptionCategoryElement> categories = ImmutableSet.of(new RuntimeOptionCategoryElement("HELP", "Nailed"));

    @Override
    public void initialize(Minecraft minecraftInstance){
        this.mc = minecraftInstance;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass(){
        return NailedConfigGuiScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
        return categories;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){
        return new RuntimeOptionGuiHandler(){

            @Override
            public void paint(int x, int y, int w, int h){

            }

            @Override
            public void close(){
            }

            @Override
            public void addWidgets(List<Gui> widgets, int x, int y, int w, int h){
                widgets.add(new GuiButton(100, x+10, y+10, "HELLO"));
            }

            @Override
            public void actionCallback(int actionId){

            }
        };
    }
}
