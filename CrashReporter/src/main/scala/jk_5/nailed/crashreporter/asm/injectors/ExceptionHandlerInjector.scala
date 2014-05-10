package jk_5.nailed.crashreporter.asm.injectors

import org.objectweb.asm.{Label, Type, Opcodes, MethodVisitor}
import org.objectweb.asm.commons.{Method => AMethod}
import jk_5.nailed.crashreporter.asm.CallHack
import org.apache.logging.log4j.LogManager
import java.util
import com.google.common.collect.Maps

/**
 * No description given
 *
 * @author jk-5
 */
object ExceptionHandlerInjector {
  val logger = LogManager.getLogger
}
class ExceptionHandlerInjector(mv: MethodVisitor, val excNames: String*) extends MethodVisitor(Opcodes.ASM4, mv) {
  final val callHackType = Type.getType(classOf[CallHack])
  final val callTarget = AMethod.getMethod(CallHack.getClass.getMethod("callForSilentException", classOf[Throwable], classOf[String]))
  final val excLabels: util.Map[Label, String] = Maps.newIdentityHashMap()
  var skipHandlers = false
  var currentLabel = 0

  override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ: String){
    super.visitTryCatchBlock(start, end, handler, typ)

    if(!skipHandlers && typ == "java/lang/Exception"){
      try{
        val name = excNames(currentLabel)
        excLabels.put(handler, name)
        currentLabel += 1
      }catch{
        case e: ArrayIndexOutOfBoundsException =>
          ExceptionHandlerInjector.logger.warn("Invalid method structure, more than two exception handlers. Aborting")
          skipHandlers = true;
      }
    }
  }

  override def visitLabel(label: Label){
    super.visitLabel(label)

    if(!skipHandlers){
      val name = excLabels.get(label)
      if(name != null) addHandler(name)
    }
  }

  def addHandler(location: String){
    ExceptionHandlerInjector.logger.info("Adding handler for '{}'", location)
    super.visitInsn(Opcodes.DUP)
    super.visitLdcInsn(location)
    super.visitMethodInsn(Opcodes.INVOKESTATIC, callHackType.getInternalName, callTarget.getName, callTarget.getDescriptor)
  }
}
