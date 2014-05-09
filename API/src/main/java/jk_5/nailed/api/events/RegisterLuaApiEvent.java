package jk_5.nailed.api.events;

import cpw.mods.fml.common.eventhandler.Event;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.scripting.ILuaAPI;

import java.util.List;

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

    public Map getMap(){
        return this.map;
    }

    public void registerApi(ILuaAPI api){
        this.apis.add(api);
    }
}
