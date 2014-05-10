package jk_5.nailed.crashreporter.asm.injectors

import org.objectweb.asm.{Type, Opcodes, MethodVisitor}
import org.objectweb.asm.commons.{Method => AMethod}
import java.io.FileWriter
import jk_5.nailed.crashreporter.asm.CallHack

/**
 * No description given
 *
 * @author jk-5
 */
class CrashHandlerInjector(mv: MethodVisitor) extends MethodVisitor(Opcodes.ASM4, mv) {
  final val fileWriterClose = AMethod.getMethod(classOf[FileWriter].getMethod("close"))
  final val callHackType = Type.getType(classOf[CallHack])
  final val callTarget = AMethod.getMethod(CallHack.getClass.getMethod("callFromCrashHandler", classOf[java.lang.Object]))

  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String){
    super.visitMethodInsn(opcode, owner, name, desc)
    if(fileWriterClose.getName.equals(name) && fileWriterClose.getDescriptor.equals(desc)){
      visitVarInsn(Opcodes.ALOAD, 0)
      visitMethodInsn(Opcodes.INVOKESTATIC, callHackType.getInternalName, callTarget.getName, callTarget.getDescriptor)
    }
  }
}
