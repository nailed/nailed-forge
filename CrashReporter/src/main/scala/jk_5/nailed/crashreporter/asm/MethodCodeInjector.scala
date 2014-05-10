package jk_5.nailed.crashreporter.asm

import org.objectweb.asm.MethodVisitor

/**
 * No description given
 *
 * @author jk-5
 */
abstract case class MethodCodeInjector(name: String, matcher: MethodMatcher) {
  def createVisitor(parent: MethodVisitor): MethodVisitor
}
