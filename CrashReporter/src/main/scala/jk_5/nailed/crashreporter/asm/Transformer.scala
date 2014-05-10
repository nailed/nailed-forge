package jk_5.nailed.crashreporter.asm

import net.minecraft.launchwrapper.IClassTransformer
import com.google.common.collect.HashMultimap
import scala.collection.JavaConversions._
import org.objectweb.asm.{ClassVisitor, ClassWriter}
import jk_5.nailed.crashreporter.asm.injectors.Injectors

/**
 * No description given
 *
 * @author jk-5
 */
class Transformer extends IClassTransformer {

  val injectors = HashMultimap.create[String, MethodCodeInjector]()

  Injectors.setupInjectors(this.injectors)

  override def transform(name: String, transformedName: String, basicClass: Array[Byte]): Array[Byte] = {
    if(basicClass == null) return basicClass
    var bytes = basicClass
    for(clsInjectors <- injectors.asMap().entrySet()){
      if(transformedName == clsInjectors.getKey){
        val methodInjector = clsInjectors.getValue
        bytes = VisitorHelper.apply(bytes, ClassWriter.COMPUTE_FRAMES, new TransformProvider {
          override def createVisitor(parent: ClassVisitor): ClassVisitor = {
            new SingleClassTransformer(parent, name, methodInjector)
          }
        })
      }
    }
    bytes
  }
}
