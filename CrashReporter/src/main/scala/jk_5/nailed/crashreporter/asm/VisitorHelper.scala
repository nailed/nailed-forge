package jk_5.nailed.crashreporter.asm

import org.objectweb.asm.{ClassWriter, ClassReader, ClassVisitor}
import com.google.common.base.Preconditions

/**
 * No description given
 *
 * @author jk-5
 */
object VisitorHelper {

  def apply(bytes: Array[Byte], flags: Int, ctx: TransformProvider): Array[Byte] = {
    Preconditions.checkNotNull(bytes)
    val cr = new ClassReader(bytes)
    val cw = new ClassWriter(cr, flags)
    val mod = ctx.createVisitor(cw)
    try{
      cr.accept(mod, 0)
      cw.toByteArray
    }catch{
      case StopTransforming => bytes
    }
  }
}

trait TransformProvider {
  def createVisitor(parent: ClassVisitor): ClassVisitor
}

object StopTransforming extends RuntimeException
