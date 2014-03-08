package jk_5.nailed.map.script.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import jk_5.nailed.map.script.IAPIEnvironment;
import jk_5.nailed.map.script.ILuaAPI;
import jk_5.nailed.map.script.ILuaContext;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class EventApi implements ILuaAPI {

    private final IAPIEnvironment env;

    private final Multimap<String, LuaClosure> eventListeners = ArrayListMultimap.create();
    private Map<Integer, LuaClosure> idListenerMap = Maps.newHashMap();
    private Map<Integer, String> idTypeMap = Maps.newHashMap();
    private AtomicInteger nextId = new AtomicInteger(0);

    @Override
    public String[] getNames(){
        return new String[]{"event", "eventbus"};
    }

    @Override
    public void startup(){

    }

    @Override
    public void advance(double paramDouble){

    }

    @Override
    public void shutdown(){
        this.eventListeners.clear();
        this.idListenerMap.clear();
        this.idTypeMap.clear();
    }

    @Override
    public String[] getMethodNames(){
        return new String[]{
                "addListener",
                "removeListener"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0: //addListener
                if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof LuaClosure){
                    int id = this.nextId.getAndIncrement();
                    String type = (String) arguments[0];
                    LuaClosure listener = (LuaClosure) arguments[1];
                    this.eventListeners.put(type, listener);
                    this.idTypeMap.put(id, type);
                    this.idListenerMap.put(id, listener);
                    return new Object[]{id};
                }else{
                    throw new Exception("Expected 1 string and 1 function argument");
                }
            case 1: //removeListener
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    int id = ((Double) arguments[0]).intValue();
                    String type = this.idTypeMap.get(id);
                    this.eventListeners.remove(type, this.idListenerMap.get(id));
                }else{
                    throw new Exception("Expected 1 int argument");
                }
        }
        return new Object[0];
    }

    public void onEvent(String eventName, Object... args){
        LuaValue[] eventArgs = this.env.getMachine().getLuaMachine().toValues(args, 0);
        for(LuaClosure listener : this.eventListeners.get(eventName)){
            listener.invoke(eventArgs);
        }
    }
}