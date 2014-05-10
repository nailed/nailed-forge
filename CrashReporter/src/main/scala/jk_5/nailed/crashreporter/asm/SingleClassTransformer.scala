package jk_5.nailed.crashreporter.asm

import org.objectweb.asm.{MethodVisitor, Opcodes, ClassVisitor}
import java.util
import scala.collection.JavaConversions._
import org.apache.logging.log4j.LogManager

/**
 * No description given
 *
 * @author jk-5
 */
object SingleClassTransformer {
  val logger = LogManager.getLogger
}
class SingleClassTransformer(val parent: ClassVisitor, val obfClassName: String, val injectors: util.Collection[MethodCodeInjector]) extends ClassVisitor(Opcodes.ASM4, parent) {

  override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
    val parent = super.visitMethod(access, name, desc, signature, exceptions)
    this.injectors.filter(_.matcher.matches(name, desc)).foreach(injector => {
      SingleClassTransformer.logger.info("Applying method transformer {} for method {}({})", injector.name, name, desc)
      return injector.createVisitor(parent)
    })
    parent
  }
}
