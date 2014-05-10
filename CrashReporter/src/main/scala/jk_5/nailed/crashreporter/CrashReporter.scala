package jk_5.nailed.crashreporter

import com.google.common.collect.HashMultiset
import _root_.org.apache.logging.log4j.LogManager
import java.io.{StringWriter, PrintWriter}
import java.util.concurrent.Executors
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import com.google.gson.Gson

/**
 * No description given
 *
 * @author jk-5
 */
object CrashReporter {

  val logger = LogManager.getLogger
  val locationCounters = HashMultiset.create[String]()
  val crashReportLimit = 20
  val executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setDaemon(false).setNameFormat("CrashReport upload Thread #%d").build())
  val gson = new Gson

  def process(throwable: Throwable, location: String){
    locationCounters.add(location)
    if(locationCounters.count(location) > crashReportLimit){
      logger.info("Limit reached for location %s, skipping %s", location, throwable)
      return
    }
    uploadCrashReport(throwable, location)
  }

  def uploadCrashReport(throwable: Throwable, location: String){
    val data = CrashReportDataCollector.populate(location)
    val runnable = new Runnable {
      override def run() = try{
        val client = HttpClients.createDefault()
        val post = new HttpPost("http://nailed.jk-5.tk/api/reportCrash/")
        val w = new StringWriter()
        val writer = new PrintWriter(w)
        throwable.printStackTrace(writer)

        val builder = MultipartEntityBuilder.create()
        builder.addTextBody("data", gson.toJson(data))
        builder.addTextBody("stacktrace", w.toString)
        post.setEntity(builder.build())

        val res = client.execute(post)
        println(res.getStatusLine.getStatusCode)
        if(res.getStatusLine.getStatusCode == 200){
          logger.info("Successfully reported exception")
        }
      }catch{
        case e: Exception => logger.warn("Error while reporting exception", e)
      }
    }
    if(location == "crash_handler"){
      runnable.run()
    }else{
      executor.execute(runnable)
    }
  }
}

case class ThrowableEntry(throwable: Throwable, location: String)
