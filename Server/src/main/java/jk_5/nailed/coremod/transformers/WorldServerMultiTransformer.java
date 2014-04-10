package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * We modify WorldServerMulti to use NailedWorldInfo instead of DerivedWorldInfo
 *
 * @author jk-5
 */
public class WorldServerMultiTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals("net.minecraft.world.WorldServerMulti")){
            ClassNode cnode = ASMHelper.createClassNode(bytes);
            MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraft/world/WorldServerMulti", "<init>", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldServer;Lnet/minecraft/profiler/Profiler;)V"), cnode);
            int offset = 0;

            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
            TypeInsnNode tn = (TypeInsnNode) mnode.instructions.get(offset);
            tn.desc = "jk_5/nailed/map/gen/NailedWorldInfo";

            while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
            mnode.instructions.remove(mnode.instructions.get(offset));
            mnode.instructions.remove(mnode.instructions.get(offset));
            MethodInsnNode mn = (MethodInsnNode) mnode.instructions.get(offset);
            mn.owner = "jk_5/nailed/map/gen/NailedWorldInfo";
            mn.desc = "(Lnet/minecraft/world/WorldServer;)V";
            mnode.instructions.insertBefore(mn, new VarInsnNode(Opcodes.ALOAD, 0));

            return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        }else{
            return bytes;
        }
    }
}
