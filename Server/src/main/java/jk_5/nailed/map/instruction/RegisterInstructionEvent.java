package jk_5.nailed.map.instruction;

import cpw.mods.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterInstructionEvent extends Event {

    private final Map<String, Class<?>> map;

    public RegisterInstructionEvent(Map<String, Class<?>> map){
        this.map = map;
    }

    public boolean register(String name, Class<?> cl){
        if(IInstruction.class.isAssignableFrom(cl)){
            if(this.map.containsKey(name) || this.map.containsValue(cl)) return false;
            this.map.put(name, cl);
            return true;
        }
        return false;
    }
}
