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

    private static final Mapping getCrashCause;
    private static final Mapping getCompleteReport;
    private static final Mapping report;
    private static final Mapping saveToFile;

    static{
        if(ASMHelper.obfuscated){
            getCrashCause = new Mapping("b", "b", "()Ljava/lang/Throwable;");
            getCompleteReport = new Mapping("b", "e", "()Ljava/lang/String;");
            saveToFile = new Mapping("b", "a", "(Ljava/io/File;)Z");
        }else{
            getCrashCause = new Mapping("net/minecraft/crash/CrashReport", "getCrashCause", "()Ljava/lang/Throwable;");
            getCompleteReport = new Mapping("net/minecraft/crash/CrashReport", "getCompleteReport", "()Ljava/lang/String;");
            saveToFile = new Mapping("net/minecraft/crash/CrashReport", "saveToFile", "(Ljava/io/File;)Z");
        }
        report = new Mapping("jk_5/nailed/crashreporter/CrashReporter", "report", "(Ljava/lang/Throwable;Ljava/lang/String;)V");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass){
        if(name.equals(saveToFile.owner.replace('/', '.'))){
            return transformCrashReport(basicClass);
        }
        return basicClass;
    }

    private byte[] transformCrashReport(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(saveToFile, cnode);

        InsnList insn = new InsnList();
        insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insn.add(getCrashCause.toInsn(Opcodes.INVOKEVIRTUAL));
        insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insn.add(getCompleteReport.toInsn(Opcodes.INVOKEVIRTUAL));
        insn.add(report.toInsn(Opcodes.INVOKESTATIC));

        int off = 0;
        while(mnode.instructions.get(off).getOpcode() != Opcodes.ALOAD) off++;

        mnode.instructions.insertBefore(mnode.instructions.get(off), insn);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
