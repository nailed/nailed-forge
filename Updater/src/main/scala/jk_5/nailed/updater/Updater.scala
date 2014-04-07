package jk_5.nailed.updater

import org.apache.logging.log4j.LogManager
import java.io.{IOException, File}
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

  def checkForUpdates(): Boolean = {
    logger.info("Checking for updates...")

    val progress = new AtomicInteger(0)

    DownloadMonitor.setNote("Reading remote versions")
    val remote = LibraryList.readFromUrl(this.versionsUrl)
    DownloadMonitor.setNote("Reading local versions")
    val local = LibraryList.readFromFile(this.versionsFile)

    DownloadMonitor.setNote("Cleaning up the mods folder...")
    val modsFolder = this.resolve("{MC_GAME_DIR}/mods/")
    if(!modsFolder.exists) modsFolder.mkdir
    DownloadMonitor.setProgress(0)
    DownloadMonitor.setMaximum(modsFolder.listFiles.length)
    modsFolder.listFiles.filter(f => f.getName.contains("Nailed") || f.getName.contains("nailed")).foreach(f => {
      logger.info(s"Found nailed related file ${f.getName} in mods folder. Removing it!")
      f.delete
      DownloadMonitor.setProgress(progress.getAndIncrement)
    })

    val download = new util.HashSet[Library]()

    DownloadMonitor.setNote("Scanning artifact versions")
    DownloadMonitor.setProgress(0)
    DownloadMonitor.setMaximum(remote.libraries.size)
    progress.set(0)
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
      DownloadMonitor.setProgress(progress.getAndIncrement)
    })

    val updated = new AtomicBoolean(false)
    val latch = new CountDownLatch(download.size)

    DownloadMonitor.setProgress(0)
    DownloadMonitor.setMaximum(download.size)
    progress.set(0)
    DownloadMonitor.setNote("Downloading updates")
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
          DownloadMonitor.setProgress(progress.getAndIncrement)
          latch.countDown()
        }
      })
    })

    latch.await()

    DownloadMonitor.setProgress(0)
    DownloadMonitor.setMaximum(0)
    progress.set(0)
    DownloadMonitor.setNote("Writing local versions file")

    if(updated.get) {
      logger.info("Writing local versions file...")
      local.writeToFile(this.versionsFile)
    }

    if(restart == RestartLevel.NOTHING) {
      logger.info("Moving artifacts to the mod folder")
      remote.libraries.foreach(library => {
        if(library.mod) {
          try{
            DownloadMonitor.setNote(s"Moving ${library.name} to the mods folder")
            logger.info(s"  Moving ${library.name} to the mods folder")
            val location = resolve(library.destination)
            val dest = resolve(s"{MC_GAME_DIR}/mods/${location.getName}")
            FileUtils.copyFile(location, dest)
            dest.deleteOnExit()
          }catch{
            case e: IOException => logger.error(s"Error while moving file ${library.name}", e)
          }
          DownloadMonitor.setProgress(progress.getAndIncrement)
        }
      })

      logger.info("Adding cascaded tweakers")
      val tweakList = Launch.blackboard.get("TweakClasses").asInstanceOf[util.List[String]]
      tweakList.addAll(remote.tweakers)

      logger.info("Injecting artifacts into classLoader")
      remote.libraries.filter(_.load).foreach(l => this.injectIntoClassLoader(this.resolve(l.destination)))

      logger.info("Injecting coremods")
      Properties.setProp("fml.coreMods.load", remote.libraries.filter(_.coremod != null).map(_.coremod).mkString(","))

      logger.info("Retrieving main class")
      this.mainClass = Option(remote.mainClass).getOrElse(this.mainClass)
    }
    DownloadMonitor.close()
    updated.get
  }

  private def resolve(input: String): File = {
    var dir = getMinecraftFolder.getAbsolutePath + "/" + input
    if(input.startsWith("{MC_GAME_DIR}")) {
      dir = input.replace("{MC_GAME_DIR}", stripTrailing(UpdatingTweaker.gameDir.getAbsolutePath))
    }
    dir = dir.replace("{MC_LIB_DIR}", "libraries")
    dir = dir.replace("{MC_VERSION_DIR}", "versions/" + UpdatingTweaker.name)
    dir = dir.replace("{MC_VERSION_NAME}", UpdatingTweaker.name)
    new File(dir)
  }

  private def stripTrailing(in: String) = if(in.endsWith("/")) in.substring(0, in.length - 1) else in
  private def getMinecraftFolder: File = {
    val userHome = Properties.propOrElse("user.home", ".")
    if(Properties.isWin && Properties.propIsSet("APPDATA")){
      new File(System.getenv("APPDATA"), ".minecraft")
    }else if(Properties.isMac){
      new File(new File(new File(userHome, "Library"), "Application Support"), "minecraft")
    }else {
      new File(userHome, ".minecraft")
    }
  }

  private def injectIntoClassLoader(file: File){
    val url = file.toURI.toURL
    if(this.addUrl == null){
      this.addUrl = classOf[URLClassLoader].getDeclaredMethod("addURL", classOf[URL])
      this.addUrl.setAccessible(true)
    }
    this.addUrl.invoke(this.classLoader.getClass.getClassLoader, url)
    this.classLoader.addURL(url)
  }
}
