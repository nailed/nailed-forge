package jk_5.nailed.crashreporter.asm

import net.minecraft.crash.CrashReport
import jk_5.nailed.crashreporter.CrashReporter
import org.apache.logging.log4j.LogManager
import net.minecraft.util.ReportedException

/**
 * No description given
 *
 * @author jk-5
 */
class CallHack
object CallHack {

  val logger = LogManager.getLogger

  def callFromCrashHandler(data: AnyRef){
    try{
      val report = data.asInstanceOf[CrashReport]
      CrashReporter.process(report.getCrashCause, "crash_handler")
    }catch{
      case t: Throwable =>
        logger.warn("Failed to store crash report {}", data)
        logger.warn("Caused by", t)
    }
  }

  def callForSilentException(throwable: Throwable, location: String){
    try{
      val t = throwable match {
        case e: ReportedException => tryExtractCause(e)
        case e => e
      }
      CrashReporter.process(t, location);
    }catch{
      case t: Throwable =>
        logger.warn("Failed to store exception %s from %s", throwable, location)
        logger.warn("Caused by", t)
    }
  }

  def tryExtractCause(e: ReportedException): Throwable = {
    try{
      e.getCrashReport.getCrashCause
    }catch{
      case t: Throwable =>
        logger.warn("Failed to extract report", t)
        CrashReporter.process(t, "crashreporter_internal")
        e
    }
  }
}
