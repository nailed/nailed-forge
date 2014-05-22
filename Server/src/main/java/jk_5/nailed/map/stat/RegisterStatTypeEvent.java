package jk_5.nailed.map.stat;

import java.util.*;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterStatTypeEvent extends Event {

    private final Map<String, IStatType> map;

    public RegisterStatTypeEvent(Map<String, IStatType> map) {
        this.map = map;
    }

    public boolean register(String name, IStatType type) {
        if(this.map.containsKey(name) || this.map.containsValue(type)){
            return false;
        }
        this.map.put(name, type);
        return true;
    }
}
