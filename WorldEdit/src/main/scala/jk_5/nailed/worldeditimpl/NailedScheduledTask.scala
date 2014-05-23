package jk_5.nailed.worldeditimpl

/**
 * No description given
 *
 * @author jk-5
 */
class NailedScheduledTask(val task: Runnable, val interval: Long = 1, var ticks: Long = 1) {

  def onTick(){
    this.ticks -= 1
    if(this.ticks == 0){
      this.task.run()
      this.ticks = this.interval
    }
  }
}
