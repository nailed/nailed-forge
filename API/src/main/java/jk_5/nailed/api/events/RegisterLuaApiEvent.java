package jk_5.nailed.api.events;

import java.util.*;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.scripting.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterLuaApiEvent extends Event {

    private final Map map;
    private final List<ILuaAPI> apis;

    public RegisterLuaApiEvent(Map map, List<ILuaAPI> apis) {
        this.map = map;
        this.apis = apis;
    }

    public Map getMap() {
        return this.map;
    }

    public void registerApi(ILuaAPI api) {
        this.apis.add(api);
    }
}
