package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class S21PacketChunkDataTransformer implements IClassTransformer {

    private final Mapping adapterMapping = new Mapping("jk_5/nailed/network/ChunkPacketAdapter", "adaptChunk", "(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;");
    private final Mapping hookMapping = new Mapping("net/minecraft/network/play/server/S21PacketChunkData", "func_149269_a", "(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals("net.minecraft.network.play.server.S21PacketChunkData")){
            return transformClass(basicClass);
        }
        return basicClass;
    }

    public byte[] transformClass(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(hookMapping, cnode);

        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ILOAD, 1));
        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
        list.add(adapterMapping.toMethodInsn(Opcodes.INVOKESTATIC));
        list.add(new InsnNode(Opcodes.ARETURN));

        mnode.instructions.insert(list);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
