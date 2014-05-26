package jk_5.nailed.coremod.asm;

import org.objectweb.asm.*;

import jk_5.nailed.coremod.transformers.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedClassWriter extends ClassWriter {

    private final boolean runtime;

    public NailedClassWriter(int flags) {
        this(flags, false);
    }

    public NailedClassWriter(int flags, boolean runtime) {
        super(flags);
        this.runtime = runtime;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        String c = type1.replace('/', '.');
        String d = type2.replace('/', '.');
        if(ClassHeirachyTransformer.classExtends(d, c)){
            return type1;
        }
        if(ClassHeirachyTransformer.classExtends(c, d)){
            return type2;
        }
        do{
            c = ClassHeirachyTransformer.getSuperClass(c, runtime);
        }
        while(!ClassHeirachyTransformer.classExtends(d, c));
        return c.replace('.', '/');
    }
}
