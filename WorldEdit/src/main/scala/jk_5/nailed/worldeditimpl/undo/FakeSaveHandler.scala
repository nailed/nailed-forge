package jk_5.nailed.worldeditimpl.undo

import net.minecraft.world.storage.{IPlayerFileData, WorldInfo, ISaveHandler}
import java.io.File
import net.minecraft.world.WorldProvider
import net.minecraft.world.chunk.storage.IChunkLoader
import net.minecraft.nbt.NBTTagCompound

/**
 * No description given
 *
 * @author jk-5
 */
class FakeSaveHandler(val worldInfo: WorldInfo) extends ISaveHandler {
  override def loadWorldInfo(): WorldInfo = worldInfo
  override def getWorldDirectory: File = null
  override def getWorldDirectoryName: String = null
  override def getSaveHandler: IPlayerFileData = null
  override def checkSessionLock(): Unit = {}
  override def saveWorldInfoWithPlayer(var1: WorldInfo, var2: NBTTagCompound): Unit = {}
  override def flush(): Unit = {}
  override def getChunkLoader(var1: WorldProvider): IChunkLoader = null
  override def getMapFileFromName(var1: String): File = null
  override def saveWorldInfo(var1: WorldInfo): Unit = {}
}
