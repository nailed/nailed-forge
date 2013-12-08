package jk_5.nailed.gui;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public enum EnumGui {
    STATEMITTER(1);

    @Getter private final int guiID;

    private static final Map<Integer, EnumGui> BY_ID = Maps.newHashMap();

    static {
        for(EnumGui gui : values()){
            BY_ID.put(gui.getGuiID(), gui);
        }
    }

    public static EnumGui fromID(int guiID){
        return BY_ID.get(guiID);
    }
}
