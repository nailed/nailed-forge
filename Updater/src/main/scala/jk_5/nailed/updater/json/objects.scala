package jk_5.nailed.updater.json

import java.io._
import java.util
import jk_5.nailed.updater.UpdatingTweaker
import org.apache.commons.io.IOUtils
import java.net.URL
import com.google.gson.GsonBuilder

/**
 * No description given
 *
 * @author jk-5
 */
class Library {
  var name: String = null
  var rev = 0
  var destination: String = null
  var location: String = null
  var restart: RestartLevel = null
  var mod = false
}

/**
 * No description given
 *
 * @author jk-5
 */
object LibraryList {

  val serializer = new GsonBuilder().registerTypeAdapterFactory(new EnumAdapterFactory).setPrettyPrinting().create

  def readFromFile(file: File): LibraryList = {
    var reader: Reader = null
    var ret: LibraryList = null
    try{
      if(file.exists) {
        reader = new FileReader(file)
        ret = serializer.fromJson(reader, classOf[LibraryList])
      }else{
        ret = new LibraryList
      }
    }catch{
      case e: Exception =>
        UpdatingTweaker.logger.error("Exception while reading local version data", e)
        ret = new LibraryList
    }finally{
      IOUtils.closeQuietly(reader)
    }
    ret
  }

  def readFromUrl(url: String): LibraryList = {
    var reader: Reader = null
    var ret: LibraryList = null
    try{
      val u: URL = new URL(url)
      reader = new InputStreamReader(u.openStream)
      ret = serializer.fromJson(reader, classOf[LibraryList])
    }catch{
      case e: Exception =>
        UpdatingTweaker.logger.error("Exception while reading remote version data", e)
        ret = new LibraryList
    }finally{
      IOUtils.closeQuietly(reader)
    }
    ret
  }
}

class LibraryList {

  var versionName: String = null
  var libraries = new util.ArrayList[Library]()
  var tweakers = new util.ArrayList[String]()

  def writeToFile(file: File){
    var writer: Writer = null
    try{
      writer = new FileWriter(file)
      LibraryList.serializer.toJson(this, writer)
    }catch {
      case e: Exception =>
    }finally{
      IOUtils.closeQuietly(writer)
    }
  }
}
