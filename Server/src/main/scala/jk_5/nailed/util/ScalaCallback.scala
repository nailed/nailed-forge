package jk_5.nailed.util

import jk_5.nailed.api.concurrent.Callback

/**
 * No description given
 *
 * @author jk-5
 */
object ScalaCallback {
  implicit def wrapCallback[T](cb: (T) => Unit) = new ScalaCallback[T](cb)
}

class ScalaCallback[T](val cb: (T) => Unit) extends Callback[T] {
  override def callback(obj: T) = cb(obj)
}
