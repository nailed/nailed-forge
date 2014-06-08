package jk_5.nailed.scheduler

import jk_5.nailed.api.concurrent.scheduler.NailedRunnable

/**
 * No description given
 *
 * @author jk-5
 */
class ScalaRunnable(val cb: () => Unit) extends NailedRunnable {
  override def run() = cb()
}

object ScalaRunnable {
  implicit def wrapRunnable(cb: () => Unit) = new ScalaRunnable(cb)
}
