package jk_5.nailed.updater

import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.concurrent.{CountDownLatch, ThreadFactory, Executors}
import jk_5.nailed.updater.json.{Library, LibraryList, RestartLevel}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import org.apache.commons.io.FileUtils
import java.net.{URLClassLoader, URL}
import scala.collection.JavaConversions._
import scala.util.Properties
import java.util
import net.minecraft.launchwrapper.Launch
import java.lang.reflect.Method

/**
 * No description given
 *
 * @author jk-5
 */
object Updater {

  lazy val minecraftFolder = {
    val userHome = Properties.propOrElse("user.home", ".")
    if(Properties.isWin && System.getenv("APPDATA") != null){
      new File(System.getenv("APPDATA"), ".minecraft")
    }else if(Properties.isMac){
      new File(new File(new File(userHome, "Library"), "Application Support"), "minecraft")
    }else {
      new File(userHome, ".minecraft")
    }
  }
  val nomonitor = Properties.propIsSet("nailed.updater.nomonitor")
  var restart = RestartLevel.NOTHING
  val logger = LogManager.getLogger("Nailed-Updater")
  val server = "http://maven.reening.nl/"
  val versionsUrl = this.server + "nailed/versions-2.json"
  val versionsFile = new File("nailedVersions.json")
  val classLoader = Launch.classLoader
  var addUrl: Method = _
  var mainClass = "net.minecraft.client.main.Main"
  val downloadThreadPool = Executors.newCachedThreadPool(new ThreadFactory {
    var id = new AtomicInteger()
    override def newThread(r: Runnable): Thread = {
      val t = new Thread(r)
      t.setDaemon(true)
      t.setName(s"DownloadThread-#${id.getAndIncrement}")
      t
    }
  })

  private[updater] def downloadUpdates(monitor: Boolean = false): (Boolean, LibraryList) = {
    logger.info("Checking for updates...")

    val progress = new AtomicInteger(0)

    val remote = LibraryList.readFromUrl(this.versionsUrl)
    val local = LibraryList.readFromFile(this.versionsFile)

    val download = new util.HashSet[Library]()

    logger.info("Scanning artifact versions...")
    remote.libraries.foreach(library => {
      val loc = local.libraries.find(_.name == library.name)
      if(loc.isEmpty){
        logger.info(s"New remote library ${library.name} will be downloaded")
        download.add(library)
      }else{
        if(library.rev > loc.get.rev) {
          logger.info(s"Library ${library.name} is outdated")
          logger.info(s"  Local rev: ${loc.get.rev}")
          logger.info(s"  Remote rev: ${library.rev}")
          download.add(library)
        }
      }
    })

    val updated = new AtomicBoolean(false)
    val latch = new CountDownLatch(download.size)

    if(monitor && download.size() > 0){
      DownloadMonitor.setProgress(0)
      DownloadMonitor.setMaximum(download.size)
      progress.set(0)
      DownloadMonitor.setNote("Downloading updates")
    }
    download.foreach(library => {
      downloadThreadPool.execute(new Runnable {
        def run(){
          logger.info(s"Starting update for ${library.name}")
          val startTime = System.currentTimeMillis
          var u = false
          try{
            val dest = resolve(library.destination)
            FileUtils.copyURLToFile(new URL(library.location), dest, 20000, 20000)
            u = true
          }catch{
            case e: Exception => logger.error(s"Error while updating file ${library.name}", e)
          }
          if(u){
            updated.set(true)
            local synchronized {
              val it = local.libraries.iterator
              while(it.hasNext) {
                val lib: Library = it.next()
                if(lib.name == library.name) it.remove()
              }
              local.libraries.add(library)
            }
            if(library.restart == RestartLevel.GAME && restart == RestartLevel.NOTHING){
              restart = RestartLevel.GAME
            }else if(library.restart == RestartLevel.LAUNCHER && (restart == RestartLevel.NOTHING || restart == RestartLevel.GAME)){
              restart = RestartLevel.LAUNCHER
            }
          }
          logger.info(s"Finished updating ${library.name} (Took ${System.currentTimeMillis() - startTime}ms)")
          if(monitor) DownloadMonitor.setProgress(progress.getAndIncrement)
          latch.countDown()
        }
      })
    })

    latch.await()

    if(updated.get) {
      if(monitor){
        DownloadMonitor.setProgress(0)
        DownloadMonitor.setMaximum(0)
        progress.set(0)
        DownloadMonitor.setNote("Writing local versions file")
      }
      logger.info("Writing local versions file...")
      local.writeToFile(this.versionsFile)
    }

    (updated.get(), remote)
  }

  def checkForUpdates(): Boolean = {
    logger.info("Cleaning up mods folder...")
    val modsFolder = this.resolve("{MC_GAME_DIR}/mods/")
    if(!modsFolder.exists) modsFolder.mkdir
    modsFolder.listFiles.filter(f => f.getName.contains("Nailed") || f.getName.contains("nailed")).foreach(f => {
      logger.info(s"Found nailed related file ${f.getName} in mods folder. Removing it!")
      f.delete
    })

    val (updated, remote) = this.downloadUpdates(monitor = !this.nomonitor)

    if(restart == RestartLevel.NOTHING) {
      logger.info("Injecting cascaded tweakers")
      val tweakList = Launch.blackboard.get("TweakClasses").asInstanceOf[util.List[String]]
      tweakList.addAll(remote.tweakers)
      remote.tweakers.foreach(t => logger.info(s"  Injected $t"))

      logger.info("Injecting artifacts into classLoader")
      remote.libraries.filter(_.load).foreach(l => this.injectIntoClassLoader(this.resolve(l.destination)))

      logger.info("Injecting coremods")
      Properties.setProp("fml.coreMods.load", remote.libraries.filter(_.coremod != null).map(_.coremod).mkString(","))
      remote.libraries.map(_.coremod).foreach(c => logger.info(s"  Injected " + c))

      logger.info("Resolving main class")
      this.mainClass = Option(remote.mainClass).getOrElse(this.mainClass)
      logger.info(s"  Main class is: ${this.mainClass}")
    }
    if(!this.nomonitor) DownloadMonitor.close()
    updated
  }

  private def resolve(input: String): File = {
    var dir = minecraftFolder.getAbsolutePath + "/" + input
    if(input.startsWith("{MC_GAME_DIR}")) {
      dir = input.replace("{MC_GAME_DIR}", stripTrailing(UpdatingTweaker.gameDir.getAbsolutePath))
    }
    dir = dir.replace("{MC_LIB_DIR}", "libraries")
    dir = dir.replace("{NAILED_LIB_DIR}", "Nailed/libs")
    dir = dir.replace("{MC_VERSION_DIR}", "versions/" + UpdatingTweaker.name)
    dir = dir.replace("{MC_VERSION_NAME}", UpdatingTweaker.name)
    new File(dir)
  }

  private def stripTrailing(in: String) = if(in.endsWith("/")) in.substring(0, in.length - 1) else in

  private def injectIntoClassLoader(file: File){
    val url = file.toURI.toURL
    logger.info(s"  Injecting ${file.getName} into classloader")
    if(this.addUrl == null){
      this.addUrl = classOf[URLClassLoader].getDeclaredMethod("addURL", classOf[URL])
      this.addUrl.setAccessible(true)
    }
    try{
      this.addUrl.invoke(this.classLoader.getClass.getClassLoader, url)
      this.classLoader.addURL(url)
    }catch{
      case e: Throwable => logger.error(s"Error while injecting ${file.getAbsolutePath} into classloader", e)
    }
  }
}
