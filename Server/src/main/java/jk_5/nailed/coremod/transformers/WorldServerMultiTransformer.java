package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.NailedFMLPlugin;
import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * We modify WorldServerMulti to use NailedWorldInfo instead of DerivedWorldInfo
 *
 * @author jk-5
 */
public class WorldServerMultiTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(transformedName.equals(TransformerData.worldServerMultiDeobfuscated.get("className"))){
            if(NailedFMLPlugin.obfuscated){
                return transformWorldServerMulti(bytes, TransformerData.worldServerMultiObfuscated);
            }else{
                return transformWorldServerMulti(bytes, TransformerData.worldServerMultiDeobfuscated);
            }
        }else return bytes;
    }

    public byte[] transformWorldServerMulti(byte[] bytes, Map<String, String> data) {
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(new Mapping(data.get("className").replace('.', '/'), "<init>", data.get("constructorSig")), cnode);
        int offset = 0;

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
        TypeInsnNode tn = (TypeInsnNode) mnode.instructions.get(offset);
        tn.desc = "jk_5/nailed/map/gen/NailedWorldInfo";

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        mnode.instructions.remove(mnode.instructions.get(offset));
        mnode.instructions.remove(mnode.instructions.get(offset));
        MethodInsnNode mn = (MethodInsnNode) mnode.instructions.get(offset);
        mn.owner = "jk_5/nailed/map/gen/NailedWorldInfo";
        mn.desc = "(Lnet/minecraft/world/WorldServer;)V";
        mnode.instructions.insertBefore(mn, new VarInsnNode(Opcodes.ALOAD, 0));

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
