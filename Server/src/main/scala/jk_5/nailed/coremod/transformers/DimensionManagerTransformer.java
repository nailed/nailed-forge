package jk_5.nailed.coremod.transformers;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.*;

import jk_5.nailed.coremod.asm.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DimensionManagerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals("net.minecraftforge.common.DimensionManager")){
            ClassNode cnode = ASMHelper.createClassNode(basicClass);
            MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraftforge/common/DimensionManager", "init", "()V"), cnode);

            int offset = 0;
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.LDC) offset++;
            LdcInsnNode ldc = (LdcInsnNode) mnode.instructions.get(offset);
            ldc.cst = Type.getType("Ljk_5/nailed/map/gen/NailedWorldProvider;");
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.POP) offset++;
            offset++;
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.POP) mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.POP) mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC) offset++;
            offset++;
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC) mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC) mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));

            return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }
        return basicClass;
    }
}
