package jk_5.nailed.crashreporter.asm

import jk_5.nailed.crashreporter.asm.injectors.Injectors
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

/**
 * No description given
 *
 * @author jk-5
 */
case class MethodMatcher(clsName: String, description: String, mcpName: String, srgName: String) {

  def matches(methodName: String, methodDesc: String): Boolean = {
    if(!methodDesc.equals(this.description)) return false
    if(methodName.equals(this.mcpName)) return true
    if(!Injectors.isSrg) return false
    val mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.clsName, methodName, methodDesc)
    mapped.equals(this.srgName)
  }
}
