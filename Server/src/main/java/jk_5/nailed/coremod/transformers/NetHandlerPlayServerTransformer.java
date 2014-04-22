package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * No description given
 *
 * @author jk-5
 */
public class NetHandlerPlayServerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals("net.minecraft.network.NetHandlerPlayServer")){
            ClassNode cnode = ASMHelper.createClassNode(bytes);
            MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraft/network/NetHandlerPlayServer", "processVanilla250Packet", "(Lnet/minecraft/network/play/client/C17PacketCustomPayload;)V"), cnode);
            int offset = -1;

            LdcInsnNode ldc;
            do{
                offset ++;
                while(mnode.instructions.get(offset).getOpcode() != Opcodes.LDC) offset ++;
                ldc = (LdcInsnNode) mnode.instructions.get(offset);
            }while(!ldc.cst.toString().equals("advMode.notEnabled"));

            while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;

            int insert = offset;

            while(mnode.instructions.get(offset).getOpcode() != Opcodes.IFEQ) mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.IFEQ) mnode.instructions.remove(mnode.instructions.get(offset));

            InsnList list = new InsnList();

            list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", "playerEntity", "Lnet/minecraft/entity/player/EntityPlayerMP;"));
            list.add(new FieldInsnNode(Opcodes.GETSTATIC, "jk_5/nailed/NailedServer", "COMMANDBLOCK_PERMISSION", "Ljava/lang/String;"));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/permissions/api/PermissionsManager", "checkPerm", "(Lnet/minecraft/entity/player/EntityPlayerMP;Ljava/lang/String;)Z"));

            mnode.instructions.insert(mnode.instructions.get(insert), list);

            Iterator<AbstractInsnNode> it = mnode.instructions.iterator();
            while(it.hasNext()){
                AbstractInsnNode node = it.next();
                System.out.println(node.toString());
            }
            System.out.println();

            return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        }else{
            return bytes;
        }
    }
}
