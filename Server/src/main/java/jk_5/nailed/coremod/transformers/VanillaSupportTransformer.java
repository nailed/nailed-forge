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
public class VanillaSupportTransformer implements IClassTransformer {

    private final Mapping timeoutMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher$VanillaTimeoutWaiter", "handlerAdded", "(Lio/netty/channel/ChannelHandlerContext;)V");
    private final Mapping kickMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher", "userEventTriggered", "(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Object;)V");
    private final Mapping handshakeMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher", "serverToClientHandshake", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V");

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
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(kickMapping, cnode);

        //Remove vanilla kick
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC) offset++;
        offset += 3;
        mnode.instructions.remove(mnode.instructions.get(offset));
        mnode.instructions.remove(mnode.instructions.get(offset));
        offset -= 1;

        //Inject a line that accepts the connection
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "cpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType", "VANILLA", "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/network/handshake/NetworkDispatcher", "completeServerSideConnection", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType;)V"));
        mnode.instructions.insert(mnode.instructions.get(offset), list);

        //Inject a pipeline hook
        mnode = ASMHelper.findMethod(handshakeMapping, cnode);
        list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/network/NailedNetworkHandler", "vanillaHandshake", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;Lnet/minecraft/entity/player/EntityPlayerMP;)V"));
        mnode.instructions.insert(list);

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
