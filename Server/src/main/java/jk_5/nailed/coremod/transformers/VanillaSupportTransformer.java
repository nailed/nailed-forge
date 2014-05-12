package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

/**
 * No description given
 *
 * @author jk-5
 */
public class VanillaSupportTransformer implements IClassTransformer {

    private final Mapping timeoutMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher$VanillaTimeoutWaiter", "handlerAdded", "(Lio/netty/channel/ChannelHandlerContext;)V");
    private final Mapping kickMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher", "userEventTriggered", "(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Object;)V");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals("cpw.mods.fml.common.network.handshake.NetworkDispatcher$VanillaTimeoutWaiter")){
            return transformTimeout(basicClass);
        }else if(name.equals("cpw.mods.fml.common.network.handshake.NetworkDispatcher")){
            return transformNetworkDispatcher(basicClass);
        }
        return basicClass;
    }

    public byte[] transformNetworkDispatcher(byte[] bytes){
        /*ClassReader reader = new ClassReader(bytes);
        reader.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);*/

        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(kickMapping, cnode);

        //cnode.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.out)));

        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC) offset++;
        offset += 3;
        mnode.instructions.remove(mnode.instructions.get(offset));
        mnode.instructions.remove(mnode.instructions.get(offset));
        offset -= 1;

        InsnList list = new InsnList();
        //completeClientSideConnection
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "cpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType", "VANILLA", "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/network/handshake/NetworkDispatcher", "completeServerSideConnection", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType;)V"));
        mnode.instructions.insert(mnode.instructions.get(offset), list);

        cnode.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.err)));

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    public byte[] transformTimeout(byte[] bytes){
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(timeoutMapping, cnode);
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.GETSTATIC) offset++;
        FieldInsnNode node = (FieldInsnNode) mnode.instructions.get(offset);
        node.name = "SECONDS";
        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
