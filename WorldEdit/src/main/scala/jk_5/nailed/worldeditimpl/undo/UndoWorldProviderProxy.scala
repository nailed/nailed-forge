package jk_5.nailed.worldeditimpl.undo

import net.minecraft.world.WorldProvider

/**
 * No description given
 *
 * @author jk-5
 */
class UndoWorldProviderProxy extends WorldProvider {
  override def getDimensionName = "undo"
  protected override def registerWorldChunkManager(){}
  protected override def generateLightBrightnessTable(){}
}
