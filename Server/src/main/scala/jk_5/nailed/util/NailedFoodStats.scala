package jk_5.nailed.util

import net.minecraft.util.FoodStats
import net.minecraft.entity.player.EntityPlayer

/**
 * No description given
 *
 * @author jk-5
 */
class NailedFoodStats extends FoodStats {

  private var minFoodLevel = 0
  private var maxFoodLevel = 20

  override def onUpdate(player: EntityPlayer){
    super.onUpdate(player)
    if(this.maxFoodLevel < this.minFoodLevel && this.maxFoodLevel > 0) return
    if(this.foodLevel < this.minFoodLevel) this.foodLevel = this.minFoodLevel
    if(this.foodLevel > this.maxFoodLevel && this.maxFoodLevel > 0) this.foodLevel = this.maxFoodLevel
  }

  def setMinFoodLevel(value: Int) = this.minFoodLevel = value
  def getMinFoodLevel = this.minFoodLevel
  def setMaxFoodLevel(value: Int) = this.maxFoodLevel = value
  def getMaxFoodLevel = this.maxFoodLevel
  def setFood(level: Int) = this.foodLevel = level
}
