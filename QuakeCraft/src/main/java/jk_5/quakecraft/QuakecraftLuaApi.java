package jk_5.quakecraft;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;

/**
 * No description given
 *
 * @author jk-5
 */
public class QuakecraftLuaApi implements ILuaAPI {

    private final Map map;

    public QuakecraftLuaApi(Map map) {
        this.map = map;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "quakecraft"
        };
    }

    @Override
    public void startup() {

    }

    @Override
    public void advance(double paramDouble) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "isQuakecraft"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method){
            case 0: //isQuakecraft
                return new Object[]{Quakecraft.instance.isQuakecraft(map.getWorld())};
        }
        return new Object[0];
    }
}
