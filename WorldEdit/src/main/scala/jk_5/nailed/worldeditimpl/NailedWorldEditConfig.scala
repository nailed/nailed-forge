package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit.util.PropertiesConfiguration
import java.io.{IOException, File}
import com.google.common.io.{ByteStreams, Files}

/**
 * No description given
 *
 * @author jk-5
 */
object NailedWorldEditConfig {

  final val propertiesFilename = "worldedit.properties"

  def getConfigFile(workingDir: File): File = {
    val configFile = new File(workingDir, this.propertiesFilename)
    try{
      if(!configFile.exists()){
        val inputstream = this.getClass.getResourceAsStream("/defaults/" + this.propertiesFilename)
        if(inputstream != null){
          val supplier = Files.newOutputStreamSupplier(configFile)
          ByteStreams.copy(inputstream, supplier)
        }
      }
    }catch{
      case e: IOException =>
    }
    configFile
  }
}

class NailedWorldEditConfig(val workingDirectory: File) extends PropertiesConfiguration(NailedWorldEditConfig.getConfigFile(workingDirectory)) {
  override def getWorkingDirectory = this.workingDirectory
  override def load(){
    super.load()
    this.showFirstUseVersion = false
  }
}
