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
public class S21PacketChunkDataTransformer implements IClassTransformer {

    private final Mapping adapterMapping = new Mapping("jk_5/nailed/network/ChunkPacketAdapter", "adaptChunk", "(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;");
    private final Mapping hookMapping = new Mapping("net/minecraft/network/play/server/S21PacketChunkData", "func_149269_a", "(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if("net.minecraft.network.play.server.S21PacketChunkData".equals(name)){
            return transformClass(basicClass);
        }
        return basicClass;
    }

    public byte[] transformClass(byte[] bytes) {
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
