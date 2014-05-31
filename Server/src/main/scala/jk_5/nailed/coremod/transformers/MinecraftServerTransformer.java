package jk_5.nailed.coremod.transformers;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.*;

import jk_5.nailed.coremod.asm.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftServerTransformer implements IClassTransformer {

    private static final String CMDMAN_CLASS = "jk_5/nailed/server/command/NailedCommandManager";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(name.equals(TransformerData.minecraftServerObfuscated.get("className"))){
            return transformMinecraftServer(bytes, TransformerData.minecraftServerObfuscated);
        }else if(name.equals(TransformerData.minecraftServerDeobfuscated.get("className"))){
            return transformMinecraftServer(bytes, TransformerData.minecraftServerDeobfuscated);
        }else{
            return bytes;
        }
    }

    public byte[] transformMinecraftServer(byte[] bytes, Map<String, String> data) {
        ClassNode cnode = ASMHelper.createClassNode(bytes, 0);

        //Find the constructor
        MethodNode mnode = ASMHelper.findMethod(new Mapping(data.get("className").replace('.', '/'), "<init>", data.get("constructorSig")), cnode);

        //Find "this.commandManager = new ServerCommandManager();"
        int offset = 0;
        int numOfNews = 0;
        while(numOfNews != 8){
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset++;
            offset++;
            numOfNews++;
        }

        //Replace vanilla's ServerCommandManager with our own NailedCommandManager
        TypeInsnNode newNode = (TypeInsnNode) mnode.instructions.get(offset - 1);
        newNode.desc = CMDMAN_CLASS;
        offset ++;
        MethodInsnNode cstrNode = (MethodInsnNode) mnode.instructions.get(offset);
        cstrNode.owner = CMDMAN_CLASS;

        //Find "this.anvilConverterForAnvilFile = new AnvilSaveConverter(p_i45281_1_);"
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset++;
        offset++;

        mnode.instructions.remove(mnode.instructions.get(offset + 1)); //Remove ALOAD 1 (The 'File' param used for the old call)

        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/io/File"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new LdcInsnNode("maps"));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/io/File;Ljava/lang/String;)V"));

        mnode.instructions.insert(mnode.instructions.get(offset), list); //Insert the list under the previous DUP

        //Find loadAllWorlds
        mnode = ASMHelper.findMethod(new Mapping(data.get("className").replace('.', '/'), data.get("targetMethod1"), data.get("targetMethod1Sig")), cnode);
        offset = 0;
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.LDC) offset++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKEVIRTUAL) offset++;

        //Inject: Map localMap = NailedAPI.getMapLoader().getLobby();
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/api/NailedAPI", "getMapLoader", "()Ljk_5/nailed/api/map/MapLoader;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jk_5/nailed/api/map/MapLoader", "getLobby", "()Ljk_5/nailed/api/map/Map;"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 12));

        mnode.instructions.insert(mnode.instructions.get(offset), list);

        //Modify ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(par2Str, true);
        //Let it use localMap.getSaveFileName() instead of par2Str
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.GETFIELD) offset++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset++;
        mnode.instructions.remove(mnode.instructions.get(offset));

        list.clear();
        list.add(new VarInsnNode(Opcodes.ALOAD, 12));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jk_5/nailed/api/map/Map", "getSaveFileName", "()Ljava/lang/String;"));

        mnode.instructions.insertBefore(mnode.instructions.get(offset), list);

        list.clear();

        //Change every reference to ALOAD2 (par2Str) to use localMap.getSaveFileName()
        while(!(mnode.instructions.get(offset).getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) mnode.instructions.get(offset)).name.equals("getStaticDimensionIDs"))){
            AbstractInsnNode node = mnode.instructions.get(offset);
            if(node.getOpcode() == Opcodes.ALOAD){
                VarInsnNode vnode = (VarInsnNode) node;
                if(vnode.var == 2){
                    list.add(new VarInsnNode(Opcodes.ALOAD, 12));
                    list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jk_5/nailed/api/map/Map", "getSaveFileName", "()Ljava/lang/String;"));
                    mnode.instructions.insert(vnode, list);
                    mnode.instructions.remove(vnode);
                    list.clear();
                }
            }
            offset++;
        }

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
