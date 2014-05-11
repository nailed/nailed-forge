package jk_5.nailed.crashreporter

import com.google.common.collect.HashMultiset
import _root_.org.apache.logging.log4j.LogManager
import java.io.{StringWriter, PrintWriter}
import java.util.concurrent.Executors
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import com.google.gson.{JsonObject, JsonParser, Gson}
import org.apache.http.util.EntityUtils
import javax.swing.{JLabel, JEditorPane, JOptionPane}
import javax.swing.event.{HyperlinkEvent, HyperlinkListener}
import java.awt.Desktop

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
  val includeUsername = Mod.config.get("includeUsername").getAsBoolean

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
        if(res.getStatusLine.getStatusCode == 200){
          logger.info("Successfully reported exception")
          val received = EntityUtils.toString(res.getEntity)
          logger.info(received)
          val data = new JsonParser().parse(received).getAsJsonObject
          if(data.get("status").getAsString == "ok"){
            if(location == "crash_handler"){
              showInfo(data)
            }
          }
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

  def showInfo(data: JsonObject){
    val crashUrl = data.get("url").getAsString
    val lbl = new JLabel
    val font = lbl.getFont
    val style = new StringBuilder("font-family:" + font.getFamily + ";")
    style.append("font-weight:" + (if(font.isBold) "bold" else "normal") + ";")
    style.append("font-size:" + font.getSize + "pt;")
    val message = if(data.get("solution").isJsonNull) "The crash has been reported. No solution was found yet.<br/>You will have to wait for an developer to add information for you.<br/>Check <a href=\"" + crashUrl + "\">" + crashUrl + "</a> for this information, and the content of the crash."
      else "The crash has been reported and a solution was found.<br/>Go to <a href=\"" + crashUrl + "\">" + crashUrl + "</a> for information on how to fix this crash."
    val ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" + message + "</body></html>")
    ep.addHyperlinkListener(new HyperlinkListener {
      override def hyperlinkUpdate(e: HyperlinkEvent){
        if(e.getEventType.equals(HyperlinkEvent.EventType.ACTIVATED)){
          Desktop.getDesktop.browse(e.getURL.toURI)
        }
      }
    })
    ep.setEditable(false)
    ep.setBackground(lbl.getBackground)
    JOptionPane.showMessageDialog(null, ep)
  }
}

case class ThrowableEntry(throwable: Throwable, location: String)
