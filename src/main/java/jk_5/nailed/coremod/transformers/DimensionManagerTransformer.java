package jk_5.nailed.coremod.transformers;

import codechicken.lib.asm.ASMHelper;
import codechicken.lib.asm.ObfMapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DimensionManagerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals("net.minecraftforge.common.DimensionManager")){
            return this.transformDimensionManager(bytes);
        }
        return bytes;
    }

    private byte[] transformDimensionManager(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes, 0);
        MethodNode mnode = ASMHelper.findMethod(new ObfMapping("net/minecraftforge/common/DimensionManager", "initDimension", "(I)V"), cnode);

        int offset = 0;
        int numOfNews = 0;
        while(numOfNews != 2){
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
            offset ++;
            numOfNews ++;
        }

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.RETURN) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ASTORE) offset ++;

        InsnList list = new InsnList();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/map/MapLoader", "instance", "()Ljk_5/nailed/map/MapLoader;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "jk_5/nailed/map/MapLoader", "getMap", "(I)Ljk_5/nailed/map/Map;"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 7));

        mnode.instructions.insert(mnode.instructions.get(offset + 2), list);
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        ((VarInsnNode) mnode.instructions.get(offset)).var = 2;
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).owner = "net/minecraft/server/MinecraftServer";
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).name = "getActiveAnvilConverter";
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).desc = "()Lnet/minecraft/world/storage/ISaveFormat;";
        list.add(new VarInsnNode(Opcodes.ALOAD, 7));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "jk_5/nailed/map/Map", "getSaveFileName", "()Ljava/lang/String;"));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraft/world/storage/ISaveFormat", "getSaveLoader", "(Ljava/lang/String;Z)Lnet/minecraft/world/storage/ISaveHandler;"));
        mnode.instructions.insert(mnode.instructions.get(offset + 1), list);
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.DUP) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.IFNE) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;

        TypeInsnNode typeNode = (TypeInsnNode) mnode.instructions.get(offset);
        typeNode.desc = "net/minecraft/world/WorldServer";

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        offset += 2;
        ((VarInsnNode) mnode.instructions.get(offset)).var = 7;
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).owner = "jk_5/nailed/map/Map";
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).name = "getSaveFileName";
        ((MethodInsnNode) mnode.instructions.get(offset + 1)).desc = "()Ljava/lang/String;";
        mnode.instructions.remove(mnode.instructions.get(offset + 2));

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ILOAD) offset ++;
        offset += 2;
        mnode.instructions.remove(mnode.instructions.get(offset));

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;
        MethodInsnNode node = (MethodInsnNode) mnode.instructions.get(offset);
        node.owner = "net/minecraft/world/WorldServer";
        node.desc = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;Lnet/minecraft/logging/ILogAgent;)V";

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
