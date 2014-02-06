package jk_5.nailed.crashreporter.transformer;

import jk_5.nailed.crashreporter.transformer.asm.ASMHelper;
import jk_5.nailed.crashreporter.transformer.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * No description given
 *
 * @author jk-5
 */
public class CrashReportTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass){
        if(name.equals("net.minecraft.crash.CrashReport")){
            return transformCrashReport(basicClass);
        }else if(name.equals("b")){
            return transformCrashReportObf(basicClass);
        }
        return basicClass;
    }

    private byte[] transformCrashReport(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraft/crash/CrashReport", "saveToFile", "(Ljava/io/File;)Z"), cnode);

        InsnList insn = new InsnList();
        insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insn.add(new Mapping("jk_5/nailed/crashreporter/CrashReporter", "report", "(Lnet/minecraft/crash/CrashReport;)V").toInsn(Opcodes.INVOKESTATIC));

        int off = 0;
        while(mnode.instructions.get(off).getOpcode() != Opcodes.ALOAD) off++;

        mnode.instructions.insertBefore(mnode.instructions.get(off), insn);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    private byte[] transformCrashReportObf(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(new Mapping("b", "a", "(Ljava/io/File;)Z"), cnode);

        InsnList insn = new InsnList();
        insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insn.add(new Mapping("jk_5/nailed/crashreporter/CrashReporter", "report", "(Lb;)V").toInsn(Opcodes.INVOKESTATIC));

        int off = 0;
        while(mnode.instructions.get(off).getOpcode() != Opcodes.ALOAD) off++;

        mnode.instructions.insertBefore(mnode.instructions.get(off), insn);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
