package jk_5.nailed.map.stat;

import lombok.RequiredArgsConstructor;
import net.minecraftforge.event.Event;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class RegisterStatTypeEvent extends Event {

    private final Map<String, IStatType> map;

    public boolean register(String name, IStatType type){
        if(this.map.containsKey(name) || this.map.containsValue(type)) return false;
        this.map.put(name, type);
        return true;
    }
}
