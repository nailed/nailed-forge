package jk_5.nailed.updater

import javax.swing.ProgressMonitor

/**
 * No description given
 *
 * @author jk-5
 */
object DownloadMonitor {
  private val monitor = new ProgressMonitor(null, "Nailed-Updater", "Checking for updates...", 0, 1)
  this.monitor.setMillisToPopup(0)
  this.monitor.setMillisToDecideToPopup(0)

  def setMaximum(max: Int) = this.monitor.setMaximum(max)
  def setNote(note: String) = this.monitor.setNote(note)
  def setProgress(progress: Int) = this.monitor.setProgress(progress)
  def close() = this.monitor.close()
}
