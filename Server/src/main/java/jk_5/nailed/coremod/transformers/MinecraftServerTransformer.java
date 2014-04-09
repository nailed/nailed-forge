package jk_5.nailed.coremod.transformers;

import jk_5.nailed.coremod.NailedFMLPlugin;
import jk_5.nailed.coremod.asm.ASMHelper;
import jk_5.nailed.coremod.asm.Mapping;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class MinecraftServerTransformer implements IClassTransformer {

    private static final String MAP_CLASS = "jk_5/nailed/map/LobbyMap";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes){
        if(transformedName.equals(TransformerData.minecraftServerDeobfuscated.get("className"))){
            if(NailedFMLPlugin.obfuscated){
                return transformMinecraftServer(bytes, TransformerData.minecraftServerObfuscated);
            }else{
                return transformMinecraftServer(bytes, TransformerData.minecraftServerDeobfuscated);
            }
        }else return bytes;
    }

    public byte[] transformMinecraftServer(byte[] bytes, Map<String, String> data) {
        ClassNode cnode = ASMHelper.createClassNode(bytes, 0);

        //Find the constructor
        MethodNode mnode = ASMHelper.findMethod(new Mapping(data.get("className").replace('.', '/'), "<init>", data.get("constructorSig")), cnode);

        int offset = 0;
        int numOfNews = 0;
        while(numOfNews != 9){
            while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
            offset ++;
            numOfNews ++;
        }
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;

        /*
         *  Inject:
         *   File localFile = new File(par1File, "maps");
         */
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new TypeInsnNode(Opcodes.NEW, "java/io/File"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new LdcInsnNode("maps"));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/io/File;Ljava/lang/String;)V"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 5));

        mnode.instructions.insertBefore(mnode.instructions.get(offset - 4), list);

        /*
         *  Hack the this.anvilConverterForAnvilFile = new AnvilSaveConverter(par1File); to use the file we created above
         */
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        VarInsnNode varNode = (VarInsnNode) mnode.instructions.get(offset);
        varNode.var = 5;

        //Find loadAllWorlds
        mnode = ASMHelper.findMethod(new Mapping(data.get("className").replace('.', '/'), data.get("targetMethod1"), data.get("targetMethod1Sig")), cnode);
        offset = 0;
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.LDC) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKEVIRTUAL) offset ++;

        //Inject: LobbyMap localLobbyMap = new LobbyMap();
        list.add(new TypeInsnNode(Opcodes.NEW, MAP_CLASS));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, MAP_CLASS, "<init>", "()V"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 12));

        mnode.instructions.insert(mnode.instructions.get(offset), list);

        //Modify ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(par2Str, true);
        //Let it use localLobbyMap.getSaveFileName() instead of par2Str
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.GETFIELD) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        mnode.instructions.remove(mnode.instructions.get(offset));

        list.clear();
        list.add(new VarInsnNode(Opcodes.ALOAD, 12));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MAP_CLASS, "getSaveFileName", "()Ljava/lang/String;"));

        mnode.instructions.insertBefore(mnode.instructions.get(offset), list);

        list.clear();

        //Change every reference to ALOAD2 (par2Str) to use localLobbyMap.getSaveFileName()
        while(!(mnode.instructions.get(offset).getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) mnode.instructions.get(offset)).name.equals("getStaticDimensionIDs"))){
            AbstractInsnNode node = mnode.instructions.get(offset);
            if(node.getOpcode() == Opcodes.ALOAD){
                VarInsnNode vnode = (VarInsnNode) node;
                if(vnode.var == 2){
                    list.add(new VarInsnNode(Opcodes.ALOAD, 12));
                    list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MAP_CLASS, "getSaveFileName", "()Ljava/lang/String;"));
                    mnode.instructions.insert(vnode, list);
                    mnode.instructions.remove(vnode);
                    list.clear();
                }
            }
            offset ++;
        }

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.IF_ICMPGE) offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ILOAD) offset ++;
        offset ++;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ILOAD) offset ++;

        //Modify the end and nether world names from DIM_-1 and DIM_1 to their nailed names (map_-1 and map_1)
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jk_5/nailed/map/NailedMapLoader", "instance", "()Ljk_5/nailed/map/NailedMapLoader;"));
        list.add(new VarInsnNode(Opcodes.ILOAD, 14));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "jk_5/nailed/map/NailedMapLoader", "getMap", "(I)Ljk_5/nailed/api/map/Map;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jk_5/nailed/api/map/Map", "getSaveFileName", "()Ljava/lang/String;"));
        list.add(new VarInsnNode(Opcodes.ASTORE, 17));

        mnode.instructions.insertBefore(mnode.instructions.get(offset), list);
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.NEW) offset ++;
        TypeInsnNode newMulti = (TypeInsnNode) mnode.instructions.get(offset);
        newMulti.desc = data.get("worldServerClass");

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.ALOAD) offset ++;
        offset ++;

        //Force nether and end to use WorldServer instead of using WorldServerMulti
        //TODO: this is bad! Remove this?
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, data.get("className").replace('.', '/'), data.get("saveFormatField"), data.get("saveFormatFieldSig")));
        list.add(new VarInsnNode(Opcodes.ALOAD, 17));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, data.get("saveFormatClass"), data.get("getSaveLoaderName"), data.get("getSaveLoaderSig")));
        list.add(new VarInsnNode(Opcodes.ALOAD, 17));

        mnode.instructions.insert(mnode.instructions.get(offset), list);
        mnode.instructions.remove(mnode.instructions.get(offset));
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKEINTERFACE) offset ++;
        mnode.instructions.remove(mnode.instructions.get(offset + 2));
        mnode.instructions.remove(mnode.instructions.get(offset + 4));
        list.clear();

        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKESPECIAL) offset ++;
        MethodInsnNode initMulti = (MethodInsnNode) mnode.instructions.get(offset);
        initMulti.owner = data.get("worldServerClass");
        initMulti.name = "<init>";
        initMulti.desc = data.get("worldServerConstructorSig");

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
