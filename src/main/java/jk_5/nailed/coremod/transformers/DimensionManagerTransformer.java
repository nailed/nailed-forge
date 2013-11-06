package jk_5.nailed.coremod.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class DimensionManagerTransformer implements IClassTransformer {

    private static final String newProvider = "Ljk_5/nailed/map/gen/NailedWorldProvider;.class";
    private static final String className = "net.minecraftforge.common.DimensionManager";
    private static final String methodName = "init";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(!name.equals(className)) return bytes;

        System.out.println("Transforming DimensionManager");
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for(MethodNode method : node.methods){
            if(methodName.equals(method.name)){
                System.out.println("Transforming DimensionManager.init");

                Iterator<AbstractInsnNode> nodes = method.instructions.iterator();
                while(nodes.hasNext()){
                    AbstractInsnNode inode = nodes.next();
                    if(inode.getOpcode() == Opcodes.LDC){
                        LdcInsnNode ldc = (LdcInsnNode) inode;
                        ldc.cst = Type.getType(newProvider);
                    }
                }
                System.out.println("Successfully patched DimensionManager.init");
            }
        }
        System.out.println("Successfully patched DimensionManager");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }
}
