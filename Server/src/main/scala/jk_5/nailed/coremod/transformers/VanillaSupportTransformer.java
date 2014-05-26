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
public class VanillaSupportTransformer implements IClassTransformer {

    private final Mapping timeoutMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher$VanillaTimeoutWaiter", "handlerAdded", "(Lio/netty/channel/ChannelHandlerContext;)V");
    private final Mapping kickMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher", "userEventTriggered", "(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Object;)V");
    private final Mapping connectedMapping = new Mapping("cpw/mods/fml/common/network/handshake/NetworkDispatcher", "completeServerSideConnection", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType;)V");
    private final Mapping modListMapping = new Mapping("cpw/mods/fml/common/network/handshake/FMLHandshakeServerState$2", "accept", "(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/handshake/FMLHandshakeMessage;)Lcpw/mods/fml/common/network/handshake/FMLHandshakeServerState;");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if("cpw.mods.fml.common.network.handshake.NetworkDispatcher$VanillaTimeoutWaiter".equals(name)){
            return transformTimeout(basicClass);
        }else if("cpw.mods.fml.common.network.handshake.NetworkDispatcher".equals(name)){
            return transformNetworkDispatcher(basicClass);
        }else if("cpw.mods.fml.common.network.handshake.FMLHandshakeServerState$2".equals(name)){
            return transformModList(basicClass);
        }
        return basicClass;
    }

    public byte[] transformNetworkDispatcher(byte[] bytes) {
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(kickMapping, cnode);

        //Remove vanilla kick
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC){
            offset++;
        }
        offset += 3;
        mnode.instructions.remove(mnode.instructions.get(offset));
        mnode.instructions.remove(mnode.instructions.get(offset));
        offset -= 1;

        //Inject a line that accepts the connection
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/network/NailedNetworkHandler", "acceptVanilla", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;)V"));
        mnode.instructions.insert(mnode.instructions.get(offset), list);

        //Inject a connection finished hook
        mnode = ASMHelper.findMethod(connectedMapping, cnode);
        list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/network/handshake/NetworkDispatcher$ConnectionType", "name", "()Ljava/lang/String;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/network/NailedNetworkHandler", "onConnected", "(Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;Ljava/lang/String;)V"));
        mnode.instructions.insert(list);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    public byte[] transformTimeout(byte[] bytes) {
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(timeoutMapping, cnode);
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.GETSTATIC){
            offset++;
        }
        FieldInsnNode node = (FieldInsnNode) mnode.instructions.get(offset);
        node.name = "SECONDS";
        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    public byte[] transformModList(byte[] bytes) {
        ClassNode cnode = ASMHelper.createClassNode(bytes);
        MethodNode mnode = ASMHelper.findMethod(modListMapping, cnode);
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ANEWARRAY){
            offset++;
        }
        offset++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ANEWARRAY){
            offset++;
        }
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC){
            offset++;
        }
        offset++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESTATIC){
            offset++;
        }
        AbstractInsnNode hook = mnode.instructions.get(offset);

        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/network/handshake/FMLHandshakeMessage$ModList", "modList", "()Ljava/util/Map;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/network/NailedNetworkHandler", "onClientModList", "(Lio/netty/channel/ChannelHandlerContext;Ljava/util/Map;)V"));

        mnode.instructions.insert(hook, list);

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
