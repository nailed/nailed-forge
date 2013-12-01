package jk_5.nailed.coremod.transformers;

import codechicken.lib.asm.ASMHelper;
import codechicken.lib.asm.ObfMapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftServerTransformer implements IClassTransformer {

    private static final String MAP_CLASS = "jk_5/nailed/map/LobbyMap";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals(TransformerData.minecraftServerDeobfuscated.get("className"))){
            return transformMinecraftServer(bytes, TransformerData.minecraftServerDeobfuscated);
        }else if(name.equals(TransformerData.minecraftServerObfuscated.get("className"))){
            return transformMinecraftServer(bytes, TransformerData.minecraftServerObfuscated);
        }else return bytes;
    }

    public byte[] transformMinecraftServer(byte[] bytes, Map<String, String> data) {
        ClassNode cnode = ASMHelper.createClassNode(bytes, 0);
        MethodNode mnode = ASMHelper.findMethod(new ObfMapping("net/minecraft/server/MinecraftServer", "<init>", "(Ljava/io/File;)V"), cnode);

        int offset = 0;
        int numOfNews = 0;
        while(numOfNews != 6){
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
            offset ++;
            numOfNews ++;
        }
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;

        System.out.println("----------------------------------BEFORE");
        System.out.println(ASMHelper.printInsnList(mnode.instructions));

        //mnode.instructions.remove(mnode.instructions.get(offset - 2));

        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, "java/io/File"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new LdcInsnNode("maps"));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/io/File;Ljava/lang/String;)V"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 5));

        mnode.instructions.insertBefore(mnode.instructions.get(offset - 4), list);

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        VarInsnNode varNode = (VarInsnNode) mnode.instructions.get(offset);
        varNode.var = 5;

        System.out.println("----------------------------------AFTER");
        System.out.println(ASMHelper.printInsnList(mnode.instructions));

        mnode = ASMHelper.findMethod(new ObfMapping("net/minecraft/server/MinecraftServer", "loadAllWorlds", "(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/WorldType;Ljava/lang/String;)V"), cnode);
        offset = 0;
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.LDC) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKEVIRTUAL) offset ++;

        list.add(new TypeInsnNode(Opcodes.NEW, MAP_CLASS));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, MAP_CLASS, "<init>", "()V"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 12));

        mnode.instructions.insert(mnode.instructions.get(offset), list);

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.GETFIELD) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        mnode.instructions.remove(mnode.instructions.get(offset));

        list.clear();
        list.add(new VarInsnNode(Opcodes.ALOAD, 12));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MAP_CLASS, "getSaveFileName", "()Ljava/lang/String;"));

        mnode.instructions.insertBefore(mnode.instructions.get(offset), list);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
