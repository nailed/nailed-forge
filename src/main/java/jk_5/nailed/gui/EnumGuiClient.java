package jk_5.nailed.gui;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@SideOnly(Side.CLIENT)
@RequiredArgsConstructor
public enum EnumGuiClient {
    STATEMITTER(1, GuiStatEmitter.class);

    @Getter private final int guiID;
    @Getter private final Class<? extends NailedGui> guiClass;

    private static final Map<Integer, EnumGuiClient> BY_ID = Maps.newHashMap();

    public NailedGui getGui(){
        try{
            return this.getGuiClass().newInstance();
        }catch(Exception e){
            return null;
        }
    }

    static {
        for(EnumGuiClient gui : values()){
            BY_ID.put(gui.getGuiID(), gui);
        }
    }

    public static EnumGuiClient fromID(int guiID){
        return BY_ID.get(guiID);
    }
}
