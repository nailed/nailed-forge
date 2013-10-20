package jk_5.nailed.coremod.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class PacketTransformer implements IClassTransformer {

    private static final String packetListener = "jk_5/nailed/network/PacketListener";
    private static final String incoming = "handleIncoming";
    private static final String outgoing = "handleOutgoing";

    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals(TransformerData.tcpConnectionDeobfuscated.get("className"))){
            return transformTcpConnection(bytes, TransformerData.tcpConnectionDeobfuscated);
        }else if(name.equals(TransformerData.tcpConnectionObfuscated.get("className"))){
            return transformTcpConnection(bytes, TransformerData.tcpConnectionObfuscated);
        }else return bytes;
    }

    public byte[] transformTcpConnection(byte[] bytes, Map<String, String> data){
        System.out.println("Transforming TcpConnection");
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for(MethodNode method : node.methods){
            if(data.get("targetMethod1").equals(method.name)){
                System.out.println("Transforming TcpConnection.addToSendQueue");
                int offset = 0;
                while (method.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;

                LabelNode lb1 = new LabelNode(new Label());
                LabelNode lb2 = new LabelNode(new Label());
                InsnList injecting = new InsnList();

                injecting.add(new VarInsnNode(Opcodes.ALOAD, 1));
                injecting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, packetListener, outgoing, "(L" + data.get("packetName") + ";)L" + data.get("packetName") + ";"));
                injecting.add(new VarInsnNode(Opcodes.ASTORE, 1));
                injecting.add(lb1);
                injecting.add(new VarInsnNode(Opcodes.ALOAD, 1));
                injecting.add(new JumpInsnNode(Opcodes.IFNONNULL, lb2));
                injecting.add(new InsnNode(Opcodes.RETURN));
                injecting.add(lb2);

                method.instructions.insertBefore(method.instructions.get(offset), injecting);
                System.out.println("Successfully patched TcpConnection.addToSendQueue");
            }else if(data.get("targetMethod2").equals(method.name)){
                System.out.println("Transforming TcpConnection.readPacket");
                int offset = 0;
                while (method.instructions.get(offset).getOpcode() != Opcodes.IFNULL) offset ++;

                LabelNode lb1 = new LabelNode(new Label());
                LabelNode lb2 = new LabelNode(new Label());
                InsnList injecting = new InsnList();

                injecting.add(new VarInsnNode(Opcodes.ALOAD, 2));
                injecting.add(new MethodInsnNode(Opcodes.INVOKESTATIC, packetListener, incoming, "(L" + data.get("packetName") + ";)L" + data.get("packetName") + ";"));
                injecting.add(new VarInsnNode(Opcodes.ASTORE, 2));
                injecting.add(lb1);
                injecting.add(new VarInsnNode(Opcodes.ALOAD, 2));
                injecting.add(new JumpInsnNode(Opcodes.IFNONNULL, lb2));
                injecting.add(new InsnNode(Opcodes.ICONST_1));
                injecting.add(new InsnNode(Opcodes.IRETURN));
                injecting.add(lb2);

                method.instructions.insert(method.instructions.get(offset), injecting);
                System.out.println("Successfully patched TcpConnection.readPacket");
            }
        }
        System.out.println("Successfully patched TcpConnection");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }
}
