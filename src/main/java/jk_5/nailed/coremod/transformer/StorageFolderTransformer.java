package jk_5.nailed.coremod.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class StorageFolderTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(name.equals(TransformerData.worldProviderDeobfuscated.get("className"))){
            return this.transformWorldProvider(bytes, TransformerData.worldProviderDeobfuscated);
        }else if(name.equals(TransformerData.worldProviderObfuscated.get("className"))){
            return this.transformWorldProvider(bytes, TransformerData.worldProviderObfuscated);
        }
        return bytes;
    }

    public byte[] transformWorldProvider(byte[] bytes, Map<String, String> data){
        System.out.println("Transforming WorldProvider");
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for(MethodNode method : node.methods){
            if(method.name.equals(data.get("targetMethod"))){
                System.out.println("Transforming WorldProvider.getSaveFolder");

                /*InsnList injecting = new InsnList();
                injecting.add(new InsnNode(Opcodes.ACONST_NULL));
                injecting.add(new InsnNode(Opcodes.RETURN));

                Iterator<AbstractInsnNode> it = method.instructions.iterator();
                AbstractInsnNode insertAfter = null;
                int index = 0;
                int labelIndex = 0;
                while(it.hasNext()){
                    AbstractInsnNode inode = it.next();
                    System.out.println(inode.getClass().getSimpleName());
                    if(index == 1) insertAfter = inode;
                    if(!(inode instanceof LabelNode) && index > 1) it.remove();
                    if(inode instanceof LabelNode && index > 1){
                        if(labelIndex < 2) it.remove();
                        labelIndex ++;
                    }
                    index ++;
                }
                method.instructions.insert(insertAfter, injecting);
                System.out.println("-----------------------------------------");
                it = method.instructions.iterator();
                while(it.hasNext()){
                    System.out.println(it.next().getClass().getSimpleName());
                }*/
                System.out.println("Successfully patched WorldProvider.getSaveFolder");
            }
        }
        System.out.println("Successfully patched WorldProvider");

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }
}
