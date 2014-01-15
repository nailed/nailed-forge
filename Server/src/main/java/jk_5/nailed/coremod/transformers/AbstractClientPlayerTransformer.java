package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class AbstractClientPlayerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals(TransformerData.abstractClientPlayerDeobfuscated.get("className"))){
            return transformAbstractClientPlayer(bytes, TransformerData.abstractClientPlayerDeobfuscated);
        }else if(name.equals(TransformerData.abstractClientPlayerObfuscated.get("className"))){
            return transformAbstractClientPlayer(bytes, TransformerData.abstractClientPlayerObfuscated);
        }else return bytes;
    }

    public byte[] transformAbstractClientPlayer(byte[] data, Map<String, String> mappings){
        ClassNode cnode = ASMHelper.createClassNode(data, 0);
        MethodNode mnode = ASMHelper.findMethod(new Mapping(mappings.get("className").replace(".", "/"), mappings.get("method1"), "(Ljava/lang/String;)Ljava/lang/String;"), cnode);

        Iterator iter = mnode.instructions.iterator();

        while (iter.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode) iter.next();
            if(node.getOpcode() == Opcodes.ARETURN){
                mnode.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                mnode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/client/NailedSkinHooks", "getSkinUrl", "(Ljava/lang/String;)Ljava/lang/String;"));
            }
        }

        mnode = ASMHelper.findMethod(new Mapping(mappings.get("className").replace(".", "/"), mappings.get("method2"), "(Ljava/lang/String;)Ljava/lang/String;"), cnode);
        iter = mnode.instructions.iterator();

        while (iter.hasNext()) {
            AbstractInsnNode node = (AbstractInsnNode) iter.next();
            if(node.getOpcode() == Opcodes.ARETURN){
                mnode.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0));
                mnode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/client/NailedSkinHooks", "getCapeUrl", "(Ljava/lang/String;)Ljava/lang/String;"));
            }
        }

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
