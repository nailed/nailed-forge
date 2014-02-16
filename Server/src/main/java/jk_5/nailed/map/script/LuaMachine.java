package jk_5.nailed.map.script;

import com.google.common.collect.Maps;
import jk_5.nailed.NailedLog;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class LuaMachine {

    private LuaValue luaGlobals;
    private LuaValue luaMainRoutine = null;
    private LuaValue luaLoadString;
    private LuaValue luaAssert;
    private LuaValue luaCoroutineCreate;
    private LuaValue luaCoroutineResume;
    private LuaValue luaCoroutineYield;

    private String softAbortMessage = null;
    private String hardAbortMessage = null;

    private String eventFilter = null;

    private Map<Map, LuaValue> processingValue;
    private static Map<LuaTable, Map> processing;
    private static List<LuaValue> tree;

    public LuaMachine(){
        this.luaGlobals = JsePlatform.debugGlobals();
        this.luaLoadString = this.luaGlobals.get("loadstring");
        this.luaAssert = this.luaGlobals.get("assert");

        LuaValue coroutine = this.luaGlobals.get("coroutine");
        final LuaValue nativeCreateCoroutine = coroutine.get("create");

        LuaValue debug = this.luaGlobals.get("debug");
        final LuaValue debugSetHook = debug.get("sethook");

        coroutine.set("create", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue){
                final LuaThread thread = nativeCreateCoroutine.call(luaValue).checkthread();
                debugSetHook.invoke(new LuaValue[]{thread, new ZeroArgFunction() {
                    @Override
                    public LuaValue call(){
                        String hardAbortMessage = LuaMachine.this.hardAbortMessage;
                        if(hardAbortMessage != null){
                            LuaThread.yield(LuaValue.NIL);
                        }
                        return LuaValue.NIL;
                    }
                }, LuaValue.NIL, LuaValue.valueOf(100000)});
                return thread;
            }
        });

        this.luaCoroutineCreate = coroutine.get("create");
        this.luaCoroutineResume = coroutine.get("resume");
        this.luaCoroutineYield = coroutine.get("yield");

        this.luaGlobals.set("collectgarbage", LuaValue.NIL);
        this.luaGlobals.set("dofile", LuaValue.NIL);
        this.luaGlobals.set("load", LuaValue.NIL);
        this.luaGlobals.set("loadfile", LuaValue.NIL);
        this.luaGlobals.set("module", LuaValue.NIL);
        this.luaGlobals.set("require", LuaValue.NIL);
        this.luaGlobals.set("package", LuaValue.NIL);
        this.luaGlobals.set("os", LuaValue.NIL);
        this.luaGlobals.set("io", LuaValue.NIL);
        this.luaGlobals.set("print", LuaValue.NIL);
        this.luaGlobals.set("luajava", LuaValue.NIL);
        this.luaGlobals.set("debug", LuaValue.NIL);
        this.luaGlobals.set("newproxy", LuaValue.NIL);
    }

    public void addAPI(ILuaAPI api){
        LuaTable table = wrapLuaObject(api);
        String[] names = api.getNames();
        for(String name : names){
            this.luaGlobals.set(name, table);
        }
    }

    public void loadBios(InputStream inputStream){
        if(this.luaMainRoutine != null){
            return;
        }

        try{
            String biosContent;
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder("");
                String line = reader.readLine();
                while(line != null){
                    content.append(line);
                    line = reader.readLine();
                    if(line != null){
                        content.append("\n");
                    }
                }
                biosContent = content.toString();
            }catch(IOException e){
                throw new LuaError("Could not read bios file");
            }
            LuaValue program = this.luaAssert.call(this.luaLoadString.call(LuaValue.valueOf(biosContent), LuaValue.valueOf("bios")));
            this.luaMainRoutine = this.luaCoroutineCreate.call(program);
        }catch(LuaError e){
            if(this.luaMainRoutine != null){
                ((LuaThread) this.luaMainRoutine).abandon();
                this.luaMainRoutine = null;
            }
        }
    }

    public void handleEvent(String eventName, Object... args){
        if(this.luaMainRoutine == null) return;
        if(this.eventFilter != null && eventName != null && !eventName.equals(this.eventFilter) && !eventName.equals("terminate")){
            return;
        }
        try{
            LuaValue[] resumeArgs;
            if(eventName != null){
                resumeArgs = toValues(args, 2);
                resumeArgs[0] = this.luaMainRoutine;
                resumeArgs[1] = LuaValue.valueOf(eventName);
            }else{
                resumeArgs = new LuaValue[1];
                resumeArgs[0] = this.luaMainRoutine;
            }

            Varargs results = this.luaCoroutineResume.invoke(LuaValue.varargsOf(resumeArgs));
            if(this.hardAbortMessage != null){
                throw new LuaError(this.hardAbortMessage);
            }
            if(!results.arg1().checkboolean()){
                throw new LuaError(results.arg(2).checkstring().toString());
            }

            LuaValue filter = results.arg(2);
            if(filter.isstring()){
                this.eventFilter = filter.toString();
            }else{
                this.eventFilter = null;
            }

            LuaThread mainThread = (LuaThread) this.luaMainRoutine;
            if(mainThread.getStatus().equals("dead")){
                this.luaMainRoutine = null;
            }
        }catch(LuaError e){
            NailedLog.error(e, "Lua error:");
            ((LuaThread) this.luaMainRoutine).abandon();
            this.luaMainRoutine = null;
        }finally{
            this.softAbortMessage = null;
            this.hardAbortMessage = null;
        }
    }

    public void abortSoft(String message){
        this.softAbortMessage = message;
    }

    public void abortHard(String message){
        this.softAbortMessage = message;
        this.hardAbortMessage = message;
    }

    public boolean isFinished(){
        return this.luaMainRoutine == null;
    }

    public void unload(){
        if(this.luaMainRoutine != null){
            LuaThread mainThread = (LuaThread) this.luaMainRoutine;
            mainThread.abandon();
            this.luaMainRoutine = null;
        }
    }

    private void tryAbort() throws LuaError{
        String abortMsg = this.softAbortMessage;
        if(abortMsg != null){
            this.softAbortMessage = null;
            this.hardAbortMessage = null;
            throw new LuaError(abortMsg);
        }
    }

    private LuaTable wrapLuaObject(ILuaObject object){
        LuaTable table = new LuaTable();
        String[] methods = object.getMethodNames();
        for (int i = 0; i < methods.length; i++){
            if (methods[i] != null){
                final int method = i;
                final ILuaObject apiObject = object;
                table.set(methods[i], new VarArgFunction(){

                    @Override
                    public Varargs invoke(Varargs _args){
                        LuaMachine.this.tryAbort();
                        Object[] arguments = LuaMachine.toObjects(_args, 1);
                        Object[] results = null;
                        try{
                            results = apiObject.callMethod(new ILuaContext(){

                                @Override
                                public Object[] pullEvent(String filter) throws Exception {
                                    Object[] results = pullEventRaw(filter);
                                    if (results.length >= 1 && results[0].equals("terminate")){
                                        throw new Exception("Terminated");
                                    }
                                    return results;
                                }

                                public Object[] pullEventRaw(String filter) throws InterruptedException {
                                    return yield(new Object[]{filter});
                                }

                                public Object[] yield(Object[] yieldArgs) throws InterruptedException{
                                    try{
                                        LuaValue[] yieldValues = LuaMachine.this.toValues(yieldArgs, 0);
                                        Varargs results = LuaMachine.this.luaCoroutineYield.invoke(LuaValue.varargsOf(yieldValues));
                                        return LuaMachine.toObjects(results, 1);
                                    }catch(OrphanedThread e){
                                        throw new InterruptedException();//FIXME: is this correct?
                                    }
                                    //Maybe here?
                                }
                            }, method, arguments);
                        }catch (InterruptedException e){
                            throw new OrphanedThread();
                        }catch (Throwable t){
                            throw new LuaError(t.getMessage());
                        }
                        return LuaValue.varargsOf(LuaMachine.this.toValues(results, 0));
                    }
                });
            }
        }
        return table;
    }

    private LuaValue toValue(Object object) {
        if(object == null){
            return LuaValue.NIL;
        }else if(object instanceof Number){
            double n = ((Number) object).doubleValue();
            return LuaValue.valueOf(n);
        }else if(object instanceof Boolean){
            boolean b = (Boolean) object;
            return LuaValue.valueOf(b);
        }else if(object instanceof String){
            String s = (String)object;
            return LuaValue.valueOf(s);
        }else if(object instanceof Map){
            boolean clearProcessing = false;
            try{
                if(this.processingValue == null){
                    this.processingValue = Maps.newIdentityHashMap();
                    clearProcessing = true;
                }
                if(this.processingValue.containsKey(object)){
                    return this.processingValue.get(object);
                }

                LuaValue table = new LuaTable();
                this.processingValue.put((Map) object, table);

                for(Map.Entry<Object, Object> e : ((Map<Object, Object>) object).entrySet()){
                    LuaValue key = toValue(e.getKey());
                    LuaValue value = toValue(e.getValue());
                    if((!key.isnil()) && (!value.isnil())){
                        table.set(key, value);
                    }
                }
                return table;
            }finally{
                if (clearProcessing){
                    this.processingValue = null;
                }
            }
        }else if(object instanceof ILuaObject){
            return wrapLuaObject((ILuaObject) object);
        }
        NailedLog.info("Could not convert object of type " + object.getClass().getName() + " to LuaValue");
        return LuaValue.NIL;
    }

    private LuaValue[] toValues(Object[] objects, int leaveEmpty){
        if(objects == null || objects.length == 0){
            return new LuaValue[leaveEmpty];
        }
        LuaValue[] values = new LuaValue[objects.length + leaveEmpty];
        for (int i = 0; i < values.length; i++){
            if (i < leaveEmpty) {
                values[i] = null;
            }else{
                Object object = objects[(i - leaveEmpty)];
                values[i] = toValue(object);
            }
        }
        return values;
    }

    private static Object toObject(LuaValue value){
        switch (value.type()){
            case -1: case 0:
                return null;
            case -2: case 3:
                return value.todouble();
            case 1:
                return value.toboolean();
            case 4:
                return value.toString();
            case 5:
                boolean clearProcessing = false;
                try{
                    if (processing == null){
                        processing = Maps.newIdentityHashMap();
                        clearProcessing = true;
                    }else if(processing.containsKey(value)){
                        return processing.get(value);
                    }
                    Map<Object, Object> ret = Maps.newHashMap();
                    processing.put((LuaTable) value, ret);

                    LuaValue k = LuaValue.NIL;
                    Varargs keyvalue;
                    while (true){
                        keyvalue = value.next(k);
                        k = keyvalue.arg1();
                        if(k.isnil()) break;
                        LuaValue v = keyvalue.arg(2);
                        Object key = toObject(k);
                        Object val = toObject(v);
                        if (key != null && (val != null)) {
                            ret.put(key, val);
                        }
                    }
                    return ret;
                }finally{
                    if (clearProcessing){
                        processing = null;
                    }
                }
            case 2:
        }

        return null;
    }

    private static Object[] toObjects(Varargs values, int startIdx){
        int count = values.narg();
        Object[] objects = new Object[count - startIdx + 1];
        for (int n = startIdx; n <= count; n++){
            int i = n - startIdx;
            LuaValue value = values.arg(n);
            objects[i] = toObject(value);
        }
        return objects;
    }
}
