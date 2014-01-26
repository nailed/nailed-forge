package jk_5.nailed.crashreporter.transformer.asm;

import com.google.common.base.Objects;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class Mapping {

    public static Remapper runtimeMapper = FMLDeobfuscatingRemapper.INSTANCE;

    public static final boolean obfuscated;

    static{
        boolean obf = true;
        try{
            obf = ((LaunchClassLoader) Mapping.class.getClassLoader()).getClassBytes("net.minecraft.world.World") == null;
        }catch(IOException iox){
        }
        obfuscated = obf;
    }

    public String owner;
    public String name;
    public String desc;

    public Mapping(String owner){
        this(owner, "", "");
    }

    public Mapping(String owner, String name, String desc){
        this.owner = owner;
        this.name = name;
        this.desc = desc;

        if(this.owner.contains(".")){
            throw new IllegalArgumentException(this.owner);
        }
    }

    public Mapping(Mapping descmap, String subclass){
        this(subclass, descmap.name, descmap.desc);
    }

    public static Mapping fromDesc(String s){
        int lastDot = s.lastIndexOf('.');
        if(lastDot < 0){
            return new Mapping(s, "", "");
        }
        int sep = s.indexOf('(');//methods
        int sep_end = sep;
        if(sep < 0){
            sep = s.indexOf(' ');//some stuffs
            sep_end = sep + 1;
        }
        if(sep < 0){
            sep = s.indexOf(':');//fields
            sep_end = sep + 1;
        }
        if(sep < 0){
            return new Mapping(s.substring(0, lastDot), s.substring(lastDot + 1), "");
        }

        return new Mapping(s.substring(0, lastDot), s.substring(lastDot + 1, sep), s.substring(sep_end));
    }

    public Mapping subclass(String subclass){
        return new Mapping(this, subclass);
    }

    public boolean matches(MethodNode node){
        return this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean matches(MethodInsnNode node){
        return this.owner.equals(node.owner) && this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    public void visitTypeInsn(MethodVisitor mv, int opcode){
        mv.visitTypeInsn(opcode, this.owner);
    }

    public void visitMethodInsn(MethodVisitor mv, int opcode){
        mv.visitMethodInsn(opcode, this.owner, this.name, this.desc);
    }

    public void visitFieldInsn(MethodVisitor mv, int opcode){
        mv.visitFieldInsn(opcode, this.owner, this.name, this.desc);
    }

    public boolean isClass(String name){
        return name.replace('.', '/').equals(this.owner);
    }

    public boolean matches(String name, String desc){
        return this.name.equals(name) && this.desc.equals(desc);
    }

    public boolean matches(FieldNode node){
        return this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    public boolean matches(FieldInsnNode node){
        return this.owner.equals(node.owner) && this.name.equals(node.name) && this.desc.equals(node.desc);
    }

    public String javaClass(){
        return this.owner.replace('/', '.');
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Mapping)){
            return false;
        }

        Mapping desc = (Mapping) obj;
        return this.owner.equals(desc.owner) && this.name.equals(desc.name) && this.desc.equals(desc.desc);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(this.desc, this.name, this.owner);
    }

    @Override
    public String toString(){
        if(this.name.length() == 0){
            return "[" + this.owner + "]";
        }
        if(this.desc.length() == 0){
            return "[" + this.owner + "." + this.name + "]";
        }
        return "[" + (isMethod() ? methodDesc() : fieldDesc()) + "]";
    }

    public String methodDesc(){
        return this.owner + "." + this.name + this.desc;
    }

    public String fieldDesc(){
        return this.owner + "." + this.name + ":" + this.desc;
    }

    public boolean isClass(){
        return this.name.length() == 0;
    }

    public boolean isMethod(){
        return this.desc.contains("(");
    }

    public boolean isField(){
        return !isClass() && !isMethod();
    }

    public Mapping map(Remapper mapper){
        if(isMethod()){
            this.name = mapper.mapMethodName(this.owner, this.name, this.desc);
        }else if(isField()){
            this.name = mapper.mapFieldName(this.owner, this.name, this.desc);
        }

        this.owner = mapper.mapType(this.owner);

        if(isMethod()){
            this.desc = mapper.mapMethodDesc(this.desc);
        }else if(this.desc.length() > 0){
            this.desc = mapper.mapDesc(this.desc);
        }

        return this;
    }

    public Mapping copy(){
        return new Mapping(this.owner, this.name, this.desc);
    }

    public MethodInsnNode toInsn(int op){
        return new MethodInsnNode(op, this.owner, this.name, this.desc);
    }
}
