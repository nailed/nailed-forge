package jk_5.nailed.updater

/**
 * No description given
 *
 * @author jk-5
 */
object UpdaterApiImpl {

  def update(): Boolean = Updater.downloadUpdates(monitor = false)._1
}
