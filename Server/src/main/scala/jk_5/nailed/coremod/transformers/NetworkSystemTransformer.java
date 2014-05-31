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
public class NetworkSystemTransformer implements IClassTransformer {

    private static final String NEW_INITIALIZER = "jk_5/nailed/network/minecraft/MinecraftChannelInitializer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals("net.minecraft.network.NetworkSystem")){
            ClassNode cnode = ASMHelper.createClassNode(basicClass);
            MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraft/network/NetworkSystem", "addLanEndpoint", "(Ljava/net/InetAddress;I)V"), cnode);
            int offset = 0;
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset++;
            offset ++;
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset++;
            TypeInsnNode newNode = (TypeInsnNode) mnode.instructions.get(offset);
            newNode.desc = NEW_INITIALIZER;
            offset += 3;
            MethodInsnNode node = (MethodInsnNode) mnode.instructions.get(offset);
            node.owner = NEW_INITIALIZER;
            return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        }else{
            return basicClass;
        }
    }
}
