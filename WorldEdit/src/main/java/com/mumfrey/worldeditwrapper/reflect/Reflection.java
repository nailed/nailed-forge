package com.mumfrey.worldeditwrapper.reflect;

import java.lang.reflect.*;

public final class Reflection {

    private static Field MODIFIERS = null;

    static {
        try{
            Reflection.MODIFIERS = (Field.class).getDeclaredField("modifiers");
            Reflection.MODIFIERS.setAccessible(true);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private Reflection() {

    }

    public static void setPrivateValue(Class<?> instanceClass, Object instance, String fieldName, Object value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        Reflection.setPrivateValueRaw(instanceClass, instance, fieldName, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrivateValue(Class<?> instanceClass, Object instance, String fieldName) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        return (T) Reflection.getPrivateValueRaw(instanceClass, instance, fieldName);
    }

    private static void setPrivateValueRaw(Class<?> instanceClass, Object instance, String fieldName, Object value) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try{
            Field field = instanceClass.getDeclaredField(fieldName);
            int modifiers = Reflection.MODIFIERS.getInt(field);

            if((modifiers & Modifier.FINAL) != 0){
                Reflection.MODIFIERS.setInt(field, modifiers & 0xffffffef);
            }

            field.setAccessible(true);
            field.set(instance, value);
        }catch(IllegalAccessException illegalaccessexception){
        }
    }

    public static Object getPrivateValueRaw(Class<?> instanceClass, Object instance, String fieldName) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try{
            Field field = instanceClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        }catch(IllegalAccessException illegalaccessexception){
            return null;
        }
    }
}
