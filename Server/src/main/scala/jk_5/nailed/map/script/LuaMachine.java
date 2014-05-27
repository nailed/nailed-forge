package jk_5.nailed.map.script;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

import io.netty.util.*;

import jk_5.nailed.*;
import jk_5.nailed.api.lua.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.lua.*;
import jk_5.nailed.map.script.api.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class LuaMachine {

    private static Map<LuaTable, Map> processing;
    private static List<LuaValue> tree;

    public LuaValue luaGlobals;
    public LuaValue luaMainRoutine = null;
    public LuaValue luaLoadString;
    public LuaValue luaAssert;
    public LuaValue luaCoroutineCreate;
    public LuaValue luaCoroutineResume;
    public LuaValue luaCoroutineYield;

    private String softAbortMessage = null;
    private String hardAbortMessage = null;

    private String eventFilter = null;

    private Map<Map, LuaValue> processingValue;

    public final  LuaConverter converter = new LuaConverter(this);

    private EventApi eventApi;

    public LuaMachine() {
        this.luaGlobals = JsePlatform.debugGlobals();
        this.luaLoadString = this.luaGlobals.get("loadstring");
        this.luaAssert = this.luaGlobals.get("assert");

        LuaValue coroutine = this.luaGlobals.get("coroutine");
        final LuaValue nativeCreateCoroutine = coroutine.get("create");

        LuaValue debug = this.luaGlobals.get("debug");
        final LuaValue debugSetHook = debug.get("sethook");

        coroutine.set("create", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                final LuaThread thread = nativeCreateCoroutine.call(luaValue).checkthread();
                debugSetHook.invoke(new LuaValue[]{thread, new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
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

    public void addAPI(Object api) {
        String[] names;
        if(api instanceof ILuaAPI){
            ILuaAPI a = (ILuaAPI) api;
            names = a.getNames();
        }else if(api.getClass().isAnnotationPresent(LuaApi.class)){
            LuaApi a = api.getClass().getAnnotation(LuaApi.class);
            names = a.value();
        }else{
            throw new InvalidLuaApiException("Given class does not have a @LuaApi annotation and is no ILuaApi");
        }
        LuaTable table = this.converter.classToValue(api);
        for(String name : names){
            this.luaGlobals.set(name, table);
        }
        if(api instanceof EventApi){
            this.eventApi = (EventApi) api;
        }
    }

    public void loadBios(InputStream inputStream) {
        if(this.luaMainRoutine != null) return;
        try{
            String biosContent;
            try{
                StringBuilder content = new StringBuilder("");
                for(String line : IOUtils.readLines(inputStream, CharsetUtil.UTF_8)){
                    content.append(line).append("\n");
                }
                biosContent = content.toString();
            }catch(IOException e){
                throw new LuaError("Could not read bios.lua");
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

    public void handleEvent(String eventName, Object... args) {
        if(this.luaMainRoutine == null){
            return;
        }
        this.eventApi.onEvent(eventName, args);
        if(this.eventFilter != null && eventName != null && !eventName.equals(this.eventFilter) && !"terminate".equals(eventName)){
            return;
        }
        try{
            LuaValue[] resumeArgs;
            if(eventName != null){
                resumeArgs = this.converter.toValues(args, 2);
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
            if("dead".equals(mainThread.getStatus())){
                this.luaMainRoutine = null;
            }
        }catch(LuaError e){
            NailedLog.error("Lua error:", e);
            ((LuaThread) this.luaMainRoutine).abandon();
            this.luaMainRoutine = null;
        }finally{
            this.softAbortMessage = null;
            this.hardAbortMessage = null;
        }
    }

    public void abortSoft(String message) {
        this.softAbortMessage = message;
    }

    public void abortHard(String message) {
        this.softAbortMessage = message;
        this.hardAbortMessage = message;
    }

    public boolean isFinished() {
        return this.luaMainRoutine == null;
    }

    public void unload() {
        if(this.luaMainRoutine != null){
            LuaThread mainThread = (LuaThread) this.luaMainRoutine;
            mainThread.abandon();
            this.luaMainRoutine = null;
        }
    }

    public void abortIfErrored() throws LuaError {
        String abortMsg = this.softAbortMessage;
        if(abortMsg != null){
            this.softAbortMessage = null;
            this.hardAbortMessage = null;
            throw new LuaError(abortMsg);
        }
    }
}
