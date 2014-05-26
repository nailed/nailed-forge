package jk_5.nailed.map.lua;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.collect.*;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import jk_5.nailed.*;
import jk_5.nailed.api.lua.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class LuaConverter {

    private static Map<LuaTable, Map> processing;
    private static Map<Map, LuaValue> processingValue;

    private final LuaMachine machine;

    public LuaConverter(LuaMachine machine) {
        this.machine = machine;
    }

    public static enum ArgType {
        BOOLEAN,
        NUMBER,
        STRING,
        TABLE
    }

    private LuaTable convertClass(final Object obj){
        LuaTable ret = new LuaTable();
        Class<?> cl = obj.getClass();
        for(final Method method : cl.getMethods()){
            if(method.isAnnotationPresent(LuaMethod.class) && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())){
                Class<?>[] argClasses = method.getParameterTypes();
                final ArgType[] argTypes = new ArgType[argClasses.length - 1];
                for(int i = 0; i < argClasses.length; i++){
                    Class<?> c = argClasses[i];
                    if(c.isPrimitive()){
                        throw new InvalidLuaClassException("Lua methods may not have primitive arguments. Use java.lang.Number");
                    }
                    if(c == Integer.class || c == Short.class || c == Byte.class || c == Long.class || c == Float.class){
                        throw new InvalidLuaClassException("Lua methods may not have other number arguments than java.lang.Number");
                    }
                    if(i == 0){
                        if(c != Context.class){
                            throw new InvalidLuaClassException("The first argument of a lua method should be Context");
                        }
                    }else{
                        if(c == Context.class){
                            throw new InvalidLuaClassException("Only the first argument of lua methods may be a Context");
                        }
                        if(Number.class.isAssignableFrom(c)){
                            argTypes[i - 1] = ArgType.NUMBER;
                        }
                        if(String.class.isAssignableFrom(c)){
                            argTypes[i - 1] = ArgType.STRING;
                        }
                        if(Boolean.class.isAssignableFrom(c)){
                            argTypes[i - 1] = ArgType.BOOLEAN;
                        }
                        if(LuaTable.class.isAssignableFrom(c)){
                            argTypes[i - 1] = ArgType.TABLE;
                        }
                    }
                }
                ret.set(method.getName(), new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs varargs) {
                        machine.abortIfErrored();
                        Object[] results = null;
                        try{
                            Object[] args = new Object[argTypes.length + 1];
                            args[0] = new Context() { //TODO: make this immutable

                            };
                            for(int i = 1; i < argTypes.length + 1; i++){
                                //ArgType type = argTypes[i - 1];
                                args[i - 1] = toObject(varargs.arg(i - 1));
                            }
                            Object result = method.invoke(obj, args);
                            if(result != null){
                                if(result.getClass().isArray()){
                                    results = (Object[]) result;
                                }else{
                                    results = new Object[]{result};
                                }
                            }
                        //}catch(InterruptedException e){
                        //    throw new OrphanedThread();
                        }catch(Throwable t){
                            throw new LuaError(t.getMessage());
                        }
                        return LuaValue.varargsOf(LuaConverter.this.toValues(results, 0));
                    }
                });
            }
        }
        return ret;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public Object toObject(LuaValue value) {
        switch(value.type()){
            case -1:
            case 0:
                return null;
            case -2:
            case 3:
                return value.todouble();
            case 1:
                return value.toboolean();
            case 4:
                return value.toString();
            case 5:
                boolean clearProcessing = false;
                try{
                    if(processing == null){
                        processing = Maps.newIdentityHashMap();
                        clearProcessing = true;
                    }else if(processing.containsKey(value)){
                        return processing.get(value);
                    }
                    Map<Object, Object> ret = Maps.newHashMap();
                    processing.put((LuaTable) value, ret);

                    LuaValue k = LuaValue.NIL;
                    Varargs keyvalue;
                    while(true){
                        keyvalue = value.next(k);
                        k = keyvalue.arg1();
                        if(k.isnil()){
                            break;
                        }
                        LuaValue v = keyvalue.arg(2);
                        Object key = toObject(k);
                        Object val = toObject(v);
                        if(key != null && (val != null)){
                            ret.put(key, val);
                        }
                    }
                    return ret;
                }finally{
                    if(clearProcessing){
                        processing = null;
                    }
                }
                //break;
                //CHECKSTYLE.OFF: all
            case 2:
                break;
            case 6:
                return value;
            default:
                NailedLog.warn("Failed to convert LuaValue to java Object");
                NailedLog.warn("  Type: " + value.type());
                NailedLog.warn("  Typename: " + value.typename());
                NailedLog.warn("  Class: " + value.getClass().getName());
                //CHECKSTYLE.ON: all
        }

        return null;
    }

    public Object[] toObjects(Varargs values, int startIdx) {
        int count = values.narg();
        Object[] objects = new Object[count - startIdx + 1];
        for(int n = startIdx; n <= count; n++){
            int i = n - startIdx;
            LuaValue value = values.arg(n);
            objects[i] = toObject(value);
        }
        return objects;
    }

    public LuaValue toValue(Object object) {
        if(object == null){
            return LuaValue.NIL;
        }else if(object instanceof Number){
            double n = ((Number) object).doubleValue();
            return LuaValue.valueOf(n);
        }else if(object instanceof Boolean){
            boolean b = (Boolean) object;
            return LuaValue.valueOf(b);
        }else if(object instanceof String){
            String s = (String) object;
            return LuaValue.valueOf(s);
        }else if(object instanceof Map){
            boolean clearProcessing = false;
            try{
                if(processingValue == null){
                    processingValue = Maps.newIdentityHashMap();
                    clearProcessing = true;
                }
                if(processingValue.containsKey(object)){
                    return processingValue.get(object);
                }

                LuaValue table = new LuaTable();
                processingValue.put((Map) object, table);

                //noinspection unchecked
                for(Map.Entry<Object, Object> e : ((Map<Object, Object>) object).entrySet()){
                    LuaValue key = toValue(e.getKey());
                    LuaValue value = toValue(e.getValue());
                    if((!key.isnil()) && (!value.isnil())){
                        table.set(key, value);
                    }
                }
                return table;
            }finally{
                if(clearProcessing){
                    processingValue = null;
                }
            }
        }else{
            LuaTable tbl = this.classToValue(object);
        }
        NailedLog.info("Could not convert object of type {} to LuaValue", object.getClass().getName());
        return LuaValue.NIL;
    }

    public LuaTable classToValue(Object obj){
        if(obj instanceof ILuaObject){
            return wrapLuaObject((ILuaObject) obj);
        }else if(obj.getClass().isAnnotationPresent(LuaConvertable.class)){
            return this.convertClass(obj);
        }
        return null;
    }

    public LuaValue[] toValues(Object[] objects, int leaveEmpty) {
        if(objects == null || objects.length == 0){
            return new LuaValue[leaveEmpty];
        }
        LuaValue[] values = new LuaValue[objects.length + leaveEmpty];
        for(int i = 0; i < values.length; i++){
            if(i < leaveEmpty){
                values[i] = null;
            }else{
                Object object = objects[i - leaveEmpty];
                values[i] = toValue(object);
            }
        }
        return values;
    }

    private LuaTable wrapLuaObject(ILuaObject object) {
        LuaTable table = new LuaTable();
        String[] methods = object.getMethodNames();
        for(int i = 0; i < methods.length; i++){
            if(methods[i] != null){
                final int method = i;
                final ILuaObject apiObject = object;
                table.set(methods[i], new VarArgFunction() {

                    @Override
                    public Varargs invoke(Varargs args) {
                        LuaConverter.this.machine.abortIfErrored();
                        Object[] arguments = LuaConverter.this.toObjects(args, 1);
                        Object[] results = null;
                        try{
                            results = apiObject.callMethod(new ILuaContext() {

                                @Override
                                public Object[] pullEvent(String filter) throws Exception {
                                    Object[] results = pullEventRaw(filter);
                                    if(results.length >= 1 && "terminate".equals(results[0])){
                                        throw new Exception("Terminated");
                                    }
                                    return results;
                                }

                                @Override
                                public Object[] pullEventRaw(String filter) throws InterruptedException {
                                    return yield(new Object[]{filter});
                                }

                                @Override
                                public Object[] yield(Object[] yieldArgs) throws InterruptedException {
                                    try{
                                        LuaValue[] yieldValues = LuaConverter.this.toValues(yieldArgs, 0);
                                        Varargs results = LuaConverter.this.machine.luaCoroutineYield.invoke(LuaValue.varargsOf(yieldValues));
                                        return LuaConverter.this.toObjects(results, 1);
                                    }catch(OrphanedThread e){
                                        throw new InterruptedException();
                                    }
                                }

                                @Override
                                public LuaValue[] toValues(Object[] objects, int leaveEmpty) {
                                    return LuaConverter.this.toValues(objects, leaveEmpty);
                                }
                            }, method, arguments);
                        }catch(InterruptedException e){
                            throw new OrphanedThread();
                        }catch(Throwable t){
                            throw new LuaError(t.getMessage());
                        }
                        return LuaValue.varargsOf(LuaConverter.this.toValues(results, 0));
                    }
                });
            }
        }
        return table;
    }
}
