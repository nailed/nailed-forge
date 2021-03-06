package jk_5.nailed.coremod.transformers;

import java.util.*;

import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.*;

import cpw.mods.fml.common.asm.transformers.deobf.*;

import jk_5.nailed.coremod.asm.*;

public class ClassHeirachyTransformer implements IClassTransformer {

    public static class SuperCache {

        public HashSet<String> parents = new HashSet<String>();
        String superclass;
        private boolean flattened;

        public void add(String parent) {
            parents.add(parent);
        }

        public void flatten() {
            if(flattened){
                return;
            }

            for(String s : new ArrayList<String>(parents)){
                SuperCache c = declareClass(s);
                if(c != null){
                    c.flatten();
                    parents.addAll(c.parents);
                }
            }
            flattened = true;
        }
    }

    public static HashMap<String, SuperCache> superclasses = new HashMap<String, SuperCache>();
    private static LaunchClassLoader cl = (LaunchClassLoader) ClassHeirachyTransformer.class.getClassLoader();

    public static String toKey(String name) {
        return name.replace('/', '.');
    }

    public static String unKey(String name) {
        return name.replace('/', '.');
    }

    /**
     * Returns true if clazz extends, either directly or indirectly, superclass.
     *
     * @param name       The class in question
     * @param superclass The class being extended
     * @return true if the given class extends the other class. Else false
     */
    public static boolean classExtends(String name, String superclass) {
        name = toKey(name);
        superclass = toKey(superclass);

        if(name.equals(superclass)){
            return true;
        }

        SuperCache cache = declareClass(name);
        if(cache == null){
            return false;
        }

        cache.flatten();
        return cache.parents.contains(superclass);
    }

    private static SuperCache declareClass(String name) {
        name = toKey(name);
        SuperCache cache = superclasses.get(name);

        if(cache != null){
            return cache;
        }

        try{
            byte[] bytes = cl.getClassBytes(unKey(name));
            if(bytes != null){
                cache = declareASM(bytes);
            }
        }catch(Exception ignored){
        }

        if(cache != null){
            return cache;
        }


        try{
            cache = declareReflection(name);
        }catch(ClassNotFoundException ignored){
        }

        return cache;
    }

    private static SuperCache declareReflection(String name) throws ClassNotFoundException {
        Class<?> aclass = Class.forName(name);

        SuperCache cache = getOrCreateCache(name);
        if(aclass.isInterface()){
            cache.superclass = "java.lang.Object";
        }else if("java.lang.Object".equals(name)){
            return cache;
        }else{
            cache.superclass = toKey(aclass.getSuperclass().getName());
        }

        cache.add(cache.superclass);
        for(Class<?> iclass : aclass.getInterfaces()){
            cache.add(toKey(iclass.getName()));
        }

        return cache;
    }

    private static SuperCache declareASM(byte[] bytes) {
        ClassNode node = ASMHelper.createClassNode(bytes);
        String name = toKey(node.name);

        SuperCache cache = getOrCreateCache(name);
        cache.superclass = toKey(node.superName.replace('/', '.'));
        cache.add(cache.superclass);
        for(String iclass : node.interfaces){
            cache.add(toKey(iclass.replace('/', '.')));
        }

        return cache;
    }

    @Override
    public byte[] transform(String name, String tname, byte[] bytes) {
        if(bytes == null){
            return null;
        }

        if(!superclasses.containsKey(tname)){
            declareASM(bytes);
        }

        return bytes;
    }

    public static SuperCache getOrCreateCache(String name) {
        SuperCache cache = superclasses.get(name);
        if(cache == null){
            superclasses.put(name, cache = new SuperCache());
        }
        return cache;
    }

    public static String getSuperClass(String name, boolean runtime) {
        name = toKey(name);
        SuperCache cache = declareClass(name);
        if(cache == null){
            return "java.lang.Object";
        }

        cache.flatten();
        String s = cache.superclass;
        if(!runtime){
            s = FMLDeobfuscatingRemapper.INSTANCE.unmap(s);
        }
        return s;
    }
}
