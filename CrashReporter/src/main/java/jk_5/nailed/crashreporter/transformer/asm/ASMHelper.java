package jk_5.nailed.crashreporter.transformer.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * No description given
 *
 * @author jk-5
 */
public class ASMHelper {

    public static MethodNode findMethod(Mapping methodmap, ClassNode cnode){
        for(MethodNode mnode : cnode.methods){
            if(methodmap.matches(mnode)){
                return mnode;
            }
        }
        return null;
    }

    public static FieldNode findField(Mapping fieldmap, ClassNode cnode){
        for(FieldNode fnode : cnode.fields){
            if(fieldmap.matches(fnode)){
                return fnode;
            }
        }
        return null;
    }

    public static ClassNode createClassNode(byte[] bytes){
        return createClassNode(bytes, 0);
    }

    public static ClassNode createClassNode(byte[] bytes, int flags){
        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(cnode, flags);
        return cnode;
    }

    public static byte[] createBytes(ClassNode cnode, int flags){
        ClassWriter cw = new ClassWriter(flags);
        cnode.accept(cw);
        return cw.toByteArray();
    }
}
