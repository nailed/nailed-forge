package jk_5.nailed.api.lua;

import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterLuaApiEvent {

    private final Map map;

    public RegisterLuaApiEvent(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void register(Object api){

    }
}
