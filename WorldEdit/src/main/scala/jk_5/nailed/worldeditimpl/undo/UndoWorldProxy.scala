package jk_5.nailed.worldeditimpl.undo

import net.minecraft.world._
import com.sk89q.worldedit.{MaxChangedBlocksException, Vector, EditSession}
import net.minecraft.world.storage.WorldInfo
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.entity.Entity
import com.sk89q.worldedit.blocks.{BaseBlock, BlockID}
import net.minecraft.block.Block
import net.minecraft.util.{AxisAlignedBB, Vec3}
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.crash.CrashReport
import net.minecraft.scoreboard.Scoreboard
import jk_5.nailed.worldeditimpl.NailedWorld
import net.minecraft.nbt.NBTTagCompound
import java.util
import net.minecraft.item.ItemStack
import net.minecraft.command.IEntitySelector
import net.minecraft.block.material.Material

/**
 * No description given
 *
 * @author jk-5
 */
object UndoWorldProxy {

  var pendingWorldInfo: WorldInfo = null

  def getWorldSettings(proxy: World): WorldSettings = {
    UndoWorldProxy.pendingWorldInfo = proxy.getWorldInfo
    new WorldSettings(UndoWorldProxy.pendingWorldInfo)
  }
}
class UndoWorldProxy(val editSession: EditSession, val proxy: World) extends World(null, "Undo", UndoWorldProxy.getWorldSettings(proxy), new UndoWorldProviderProxy, proxy.theProfiler) {
  UndoWorldProxy.pendingWorldInfo = null

  def createChunkProvider: IChunkProvider = null
  def getEntityByID(var1: Int): Entity = null
  override def calculateInitialSkylight() = {}

  def getTypeId(x: Int, y: Int, z: Int) = this.editSession.getBlockType(new Vector(x, y, z))
  def isEmpty(x: Int, y: Int, z: Int) = this.editSession.getBlockType(new Vector(x, y, z)) == BlockID.AIR

  override def getBlock(x: Int, y: Int, z: Int) = NailedWorld.getBlockById(this.editSession.getBlockType(new Vector(x, y, z)))

  override def setBlock(x: Int, y: Int, z: Int, block: Block): Boolean = {
    try{
      this.editSession.setBlock(new Vector(x, y, z), NailedWorld.getBaseBlock(block))
    }catch{
      case ex: MaxChangedBlocksException => false
    }
  }

  override def setBlock(x: Int, y: Int, z: Int, block: Block, metaData: Int, flags: Int): Boolean = {
    try{
      this.editSession.setBlock(new Vector(x, y, z), NailedWorld.getBaseBlock(block, metaData))
    }catch{
      case ex: MaxChangedBlocksException => false
    }
  }

  override def setBlockMetadataWithNotify(x: Int, y: Int, z: Int, metaData: Int, flags: Int) = false

  override def setBlockToAir(x: Int, y: Int, z: Int): Boolean = {
    try{
      this.editSession.setBlock(new Vector(x, y, z), new BaseBlock(0))
    }catch{
      case ex: MaxChangedBlocksException => false
    }
  }

  override def getWorldInfo: WorldInfo = if (this.proxy == null) UndoWorldProxy.pendingWorldInfo else this.proxy.getWorldInfo

  override def getBiomeGenForCoords(par1: Int, par2: Int) = this.proxy.getBiomeGenForCoords(par1, par2)
  override def getWorldChunkManager = this.proxy.getWorldChunkManager
  override def initialize(par1WorldSettings: WorldSettings){}
  override def setSpawnLocation() = this.proxy.setSpawnLocation()
  override def getTopBlock(x: Int, z: Int) = this.proxy.getTopBlock(x, z)
  override def isAirBlock(x: Int, y: Int, z: Int) = this.proxy.isAirBlock(x, y, z)
  override def blockExists(par1: Int, par2: Int, par3: Int) = this.proxy.blockExists(par1, par2, par3)
  override def doChunksNearChunkExist(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.doChunksNearChunkExist(par1, par2, par3, par4)
  override def checkChunksExist(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) = this.proxy.checkChunksExist(par1, par2, par3, par4, par5, par6)
  override def chunkExists(par1: Int, par2: Int) = true
  override def getChunkFromBlockCoords(par1: Int, par2: Int) = this.proxy.getChunkFromBlockCoords(par1, par2)
  override def getChunkFromChunkCoords(par1: Int, par2: Int) = this.proxy.getChunkFromChunkCoords(par1, par2)
  override def getBlockMetadata(par1: Int, par2: Int, par3: Int) = this.proxy.getBlockMetadata(par1, par2, par3)
  override def func_147480_a(x: Int, y: Int, z: Int, b: Boolean) = this.proxy.func_147480_a(x, y, z, b)
  override def markBlockForUpdate(x: Int, y: Int, z: Int) = this.proxy.markBlockForUpdate(x, y, z)
  override def notifyBlockChange(x: Int, y: Int, z: Int, block: Block) = this.proxy.notifyBlockChange(x, y, z, block)
  override def markBlocksDirtyVertical(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.markBlocksDirtyVertical(par1, par2, par3, par4)
  override def markBlockRangeForRenderUpdate(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) = this.proxy.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2)
  override def notifyBlocksOfNeighborChange(x: Int, y: Int, z: Int, block: Block) = this.proxy.notifyBlocksOfNeighborChange(x, y, z, block)
  override def notifyBlocksOfNeighborChange(x: Int, y: Int, z: Int, block: Block, meta: Int) = this.proxy.notifyBlocksOfNeighborChange(x, y, z, block, meta)
  override def notifyBlockOfNeighborChange(x: Int, y: Int, z: Int, block: Block) = this.proxy.notifyBlockOfNeighborChange(x, y, z, block)
  override def isBlockTickScheduledThisTick(x: Int, y: Int, z: Int, block: Block) = this.proxy.isBlockTickScheduledThisTick(x, y, z, block)
  override def canBlockSeeTheSky(par1: Int, par2: Int, par3: Int) = this.proxy.canBlockSeeTheSky(par1, par2, par3)
  override def getFullBlockLightValue(par1: Int, par2: Int, par3: Int) = this.proxy.getFullBlockLightValue(par1, par2, par3)
  override def getBlockLightValue(par1: Int, par2: Int, par3: Int) = this.proxy.getBlockLightValue(par1, par2, par3)
  override def getBlockLightValue_do(par1: Int, par2: Int, par3: Int, par4: Boolean) = this.proxy.getBlockLightValue_do(par1, par2, par3, par4)
  override def getHeightValue(par1: Int, par2: Int) = this.proxy.getHeightValue(par1, par2)
  override def getChunkHeightMapMinimum(par1: Int, par2: Int) = this.proxy.getChunkHeightMapMinimum(par1, par2)
  override def getSkyBlockTypeBrightness(par1EnumSkyBlock: EnumSkyBlock, par2: Int, par3: Int, par4: Int) = this.proxy.getSkyBlockTypeBrightness(par1EnumSkyBlock, par2, par3, par4)
  override def getSavedLightValue(par1EnumSkyBlock: EnumSkyBlock, par2: Int, par3: Int, par4: Int) = this.proxy.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4)
  override def setLightValue(par1EnumSkyBlock: EnumSkyBlock, par2: Int, par3: Int, par4: Int, par5: Int) = this.proxy.setLightValue(par1EnumSkyBlock, par2, par3, par4, par5)
  override def func_147479_m(x: Int, y: Int, z: Int) = this.proxy.func_147479_m(x, y, z)
  override def getLightBrightnessForSkyBlocks(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.getLightBrightnessForSkyBlocks(par1, par2, par3, par4)
  override def getLightBrightness(par1: Int, par2: Int, par3: Int) = this.proxy.getLightBrightness(par1, par2, par3)
  override def isDaytime = this.proxy.isDaytime
  override def rayTraceBlocks(par1Vec3: Vec3, par2Vec3: Vec3) = this.proxy.rayTraceBlocks(par1Vec3, par2Vec3)
  override def rayTraceBlocks(par1Vec3: Vec3, par2Vec3: Vec3, par3: Boolean) = this.proxy.rayTraceBlocks(par1Vec3, par2Vec3, par3)
  override def func_147447_a(start: Vec3, end: Vec3, b1: Boolean, b2: Boolean, b3: Boolean) = this.proxy.func_147447_a(start, end, b1, b2, b3)
  override def playSoundAtEntity(par1Entity: Entity, par2Str: String, par3: Float, par4: Float) = this.proxy.playSoundAtEntity(par1Entity, par2Str, par3, par4)
  override def playSoundToNearExcept(par1EntityPlayer: EntityPlayer, par2Str: String, par3: Float, par4: Float) = this.proxy.playSoundToNearExcept(par1EntityPlayer, par2Str, par3, par4)
  override def playSoundEffect(par1: Double, par3: Double, par5: Double, par7Str: String, par8: Float, par9: Float) = this.proxy.playSoundEffect(par1, par3, par5, par7Str, par8, par9)
  override def playSound(par1: Double, par3: Double, par5: Double, par7Str: String, par8: Float, par9: Float, par10: Boolean) = this.proxy.playSound(par1, par3, par5, par7Str, par8, par9, par10)
  override def playRecord(par1Str: String, par2: Int, par3: Int, par4: Int) = this.proxy.playRecord(par1Str, par2, par3, par4)
  override def spawnParticle(par1Str: String, par2: Double, par4: Double, par6: Double, par8: Double, par10: Double, par12: Double) = this.proxy.spawnParticle(par1Str, par2, par4, par6, par8, par10, par12)
  override def addWeatherEffect(par1Entity: Entity) = this.proxy.addWeatherEffect(par1Entity)
  override def spawnEntityInWorld(par1Entity: Entity) = this.proxy.spawnEntityInWorld(par1Entity)
  override def onEntityAdded(par1Entity: Entity){}
  override def onEntityRemoved(par1Entity: Entity){}
  override def removeEntity(par1Entity: Entity) = this.proxy.removeEntity(par1Entity)
  override def removePlayerEntityDangerously(par1Entity: Entity) = this.proxy.removePlayerEntityDangerously(par1Entity)
  override def addWorldAccess(par1iWorldAccess: IWorldAccess) = this.proxy.addWorldAccess(par1iWorldAccess)
  override def removeWorldAccess(par1iWorldAccess: IWorldAccess) = this.proxy.removeWorldAccess(par1iWorldAccess)
  override def getCollidingBoundingBoxes(par1Entity: Entity, par2AxisAlignedBB: AxisAlignedBB) = this.proxy.getCollidingBoundingBoxes(par1Entity, par2AxisAlignedBB)
  override def func_147461_a(aabb: AxisAlignedBB) = this.proxy.func_147461_a(aabb)
  override def calculateSkylightSubtracted(par1: Float) = this.proxy.calculateSkylightSubtracted(par1)
  override def getSunBrightness(par1: Float) = this.proxy.getSunBrightness(par1)
  override def getSkyColor(par1Entity: Entity, par2: Float) = this.proxy.getSkyColor(par1Entity, par2)
  override def getCelestialAngle(par1: Float) = this.proxy.getCelestialAngle(par1)
  override def getMoonPhase = this.proxy.getMoonPhase
  override def getCurrentMoonPhaseFactor = this.proxy.getCurrentMoonPhaseFactor
  override def getCelestialAngleRadians(par1: Float) = this.proxy.getCelestialAngleRadians(par1)
  override def getCloudColour(par1: Float) = this.proxy.getCloudColour(par1)
  override def getFogColor(par1: Float) = this.proxy.getFogColor(par1)
  override def getPrecipitationHeight(par1: Int, par2: Int) = this.proxy.getPrecipitationHeight(par1, par2)
  override def getTopSolidOrLiquidBlock(par1: Int, par2: Int) = this.proxy.getTopSolidOrLiquidBlock(par1, par2)
  override def getStarBrightness(par1: Float) = this.proxy.getStarBrightness(par1)
  override def scheduleBlockUpdate(x: Int, y: Int, z: Int, block: Block, meta: Int) = this.proxy.scheduleBlockUpdate(x, y, z, block, meta)
  override def scheduleBlockUpdateWithPriority(x: Int, y: Int, z: Int, block: Block, meta: Int, prio: Int) = this.proxy.scheduleBlockUpdateWithPriority(x, y, z, block, meta, prio)
  override def func_147446_b(i1: Int, i2: Int, i3: Int, block: Block, i4: Int, i5: Int) = this.proxy.func_147446_b(i1, i2, i3, block, i4, i5)
  override def updateEntities() = this.proxy.updateEntities()
  override def func_147448_a(list: util.Collection[_]) = this.proxy.func_147448_a(list)
  override def updateEntity(par1Entity: Entity) = this.proxy.updateEntity(par1Entity)
  override def updateEntityWithOptionalForce(par1Entity: Entity, par2: Boolean) = this.proxy.updateEntityWithOptionalForce(par1Entity, par2)
  override def checkNoEntityCollision(par1AxisAlignedBB: AxisAlignedBB) = this.proxy.checkNoEntityCollision(par1AxisAlignedBB)
  override def checkNoEntityCollision(par1AxisAlignedBB: AxisAlignedBB, par2Entity: Entity) = this.proxy.checkNoEntityCollision(par1AxisAlignedBB, par2Entity)
  override def checkBlockCollision(par1AxisAlignedBB: AxisAlignedBB) = this.proxy.checkBlockCollision(par1AxisAlignedBB)
  override def isAnyLiquid(par1AxisAlignedBB: AxisAlignedBB) = this.proxy.isAnyLiquid(par1AxisAlignedBB)
  override def func_147470_e(aabb: AxisAlignedBB) = this.proxy.func_147470_e(aabb)
  override def handleMaterialAcceleration(par1AxisAlignedBB: AxisAlignedBB, par2Material: Material, par3Entity: Entity) = this.proxy.handleMaterialAcceleration(par1AxisAlignedBB, par2Material, par3Entity)
  override def isMaterialInBB(par1AxisAlignedBB: AxisAlignedBB, par2Material: Material) = this.proxy.isMaterialInBB(par1AxisAlignedBB, par2Material)
  override def isAABBInMaterial(par1AxisAlignedBB: AxisAlignedBB, par2Material: Material) = this.proxy.isAABBInMaterial(par1AxisAlignedBB, par2Material)
  override def createExplosion(par1Entity: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Boolean) = this.proxy.createExplosion(par1Entity, par2, par4, par6, par8, par9)
  override def newExplosion(par1Entity: Entity, par2: Double, par4: Double, par6: Double, par8: Float, par9: Boolean, par10: Boolean) = this.proxy.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10)
  override def getBlockDensity(par1Vec3: Vec3, par2AxisAlignedBB: AxisAlignedBB) = this.proxy.getBlockDensity(par1Vec3, par2AxisAlignedBB)
  override def extinguishFire(par1EntityPlayer: EntityPlayer, par2: Int, par3: Int, par4: Int, par5: Int) = this.proxy.extinguishFire(par1EntityPlayer, par2, par3, par4, par5)
  override def getDebugLoadedEntities = this.proxy.getDebugLoadedEntities
  override def getProviderName = this.proxy.getProviderName
  override def getTileEntity(x: Int, y: Int, z: Int) = this.proxy.getTileEntity(x, y, z)
  override def setTileEntity(x: Int, y: Int, z: Int, tile: TileEntity) = this.proxy.setTileEntity(x, y, z, tile)
  override def removeTileEntity(x: Int, y: Int, z: Int) = this.proxy.removeTileEntity(x, y, z)
  override def func_147457_a(tile: TileEntity) = this.proxy.func_147457_a(tile)
  override def func_147469_q(x: Int, y: Int, z: Int) = this.proxy.func_147469_q(x, y, z)
  override def isBlockNormalCubeDefault(x: Int, y: Int, z: Int, b: Boolean) = this.proxy.isBlockNormalCubeDefault(x, y, z, b)
  override def setAllowedSpawnTypes(par1: Boolean, par2: Boolean) = this.proxy.setAllowedSpawnTypes(par1, par2)
  override def tick() = this.proxy.tick()
  override def updateWeather(){}
  override def setActivePlayerChunksAndCheckLight(){}
  override def func_147467_a(x: Int, z: Int, chunk: Chunk){}
  override def func_147456_g(){}
  override def isBlockFreezable(par1: Int, par2: Int, par3: Int) = this.proxy.isBlockFreezable(par1, par2, par3)
  override def isBlockFreezableNaturally(par1: Int, par2: Int, par3: Int) = this.proxy.isBlockFreezableNaturally(par1, par2, par3)
  override def canBlockFreeze(par1: Int, par2: Int, par3: Int, par4: Boolean) = this.proxy.canBlockFreeze(par1, par2, par3, par4)
  override def func_147478_e(x: Int, y: Int, z: Int, b: Boolean) = this.proxy.func_147478_e(x, y, z, b)
  override def func_147451_t(x: Int, y: Int, z: Int) = this.proxy.func_147451_t(x, y, z)
  override def updateLightByType(`type`: EnumSkyBlock, x: Int, y: Int, z: Int) = this.proxy.updateLightByType(`type`, x, y, z)
  override def tickUpdates(par1: Boolean) = this.proxy.tickUpdates(par1)
  override def getPendingBlockUpdates(par1Chunk: Chunk, par2: Boolean) = this.proxy.getPendingBlockUpdates(par1Chunk, par2)
  override def getEntitiesWithinAABBExcludingEntity(par1Entity: Entity, par2AxisAlignedBB: AxisAlignedBB) = this.proxy.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB)
  override def getEntitiesWithinAABBExcludingEntity(par1Entity: Entity, par2AxisAlignedBB: AxisAlignedBB, par3iEntitySelector: IEntitySelector) = this.proxy.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB, par3iEntitySelector)
  override def getEntitiesWithinAABB(par1Class: Class[_], par2AxisAlignedBB: AxisAlignedBB) = this.proxy.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB)
  override def selectEntitiesWithinAABB(par1Class: Class[_], par2AxisAlignedBB: AxisAlignedBB, par3iEntitySelector: IEntitySelector) = this.proxy.selectEntitiesWithinAABB(par1Class, par2AxisAlignedBB, par3iEntitySelector)
  override def findNearestEntityWithinAABB(par1Class: Class[_], par2AxisAlignedBB: AxisAlignedBB, par3Entity: Entity) = this.proxy.findNearestEntityWithinAABB(par1Class, par2AxisAlignedBB, par3Entity)
  override def getLoadedEntityList = this.proxy.getLoadedEntityList
  override def markTileEntityChunkModified(x: Int, y: Int, z: Int, tile: TileEntity) = this.proxy.markTileEntityChunkModified(x, y, z, tile)
  override def countEntities(par1Class: Class[_]) = this.proxy.countEntities(par1Class)
  override def addLoadedEntities(par1List: util.List[_]) = this.proxy.addLoadedEntities(par1List)
  override def unloadEntities(par1List: util.List[_]) = this.proxy.unloadEntities(par1List)
  override def canPlaceEntityOnSide(block: Block, x: Int, y: Int, z: Int, b: Boolean, i: Int, entity: Entity, stack: ItemStack) = this.proxy.canPlaceEntityOnSide(block, x, y, z, b, i, entity, stack)
  override def getPathEntityToEntity(par1Entity: Entity, par2Entity: Entity, par3: Float, par4: Boolean, par5: Boolean, par6: Boolean, par7: Boolean) = this.proxy.getPathEntityToEntity(par1Entity, par2Entity, par3, par4, par5, par6, par7)
  override def getEntityPathToXYZ(par1Entity: Entity, par2: Int, par3: Int, par4: Int, par5: Float, par6: Boolean, par7: Boolean, par8: Boolean, par9: Boolean) = this.proxy.getEntityPathToXYZ(par1Entity, par2, par3, par4, par5, par6, par7, par8, par9)
  override def isBlockProvidingPowerTo(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.isBlockProvidingPowerTo(par1, par2, par3, par4)
  override def getBlockPowerInput(par1: Int, par2: Int, par3: Int) = this.proxy.getBlockPowerInput(par1, par2, par3)
  override def getIndirectPowerOutput(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.getIndirectPowerOutput(par1, par2, par3, par4)
  override def getIndirectPowerLevelTo(par1: Int, par2: Int, par3: Int, par4: Int) = this.proxy.getIndirectPowerLevelTo(par1, par2, par3, par4)
  override def isBlockIndirectlyGettingPowered(par1: Int, par2: Int, par3: Int) = this.proxy.isBlockIndirectlyGettingPowered(par1, par2, par3)
  override def getStrongestIndirectPower(par1: Int, par2: Int, par3: Int) = this.proxy.getStrongestIndirectPower(par1, par2, par3)
  override def getClosestPlayerToEntity(par1Entity: Entity, par2: Double) = this.proxy.getClosestPlayerToEntity(par1Entity, par2)
  override def getClosestPlayer(par1: Double, par3: Double, par5: Double, par7: Double) = this.proxy.getClosestPlayer(par1, par3, par5, par7)
  override def getClosestVulnerablePlayerToEntity(par1Entity: Entity, par2: Double) = this.proxy.getClosestVulnerablePlayerToEntity(par1Entity, par2)
  override def getClosestVulnerablePlayer(par1: Double, par3: Double, par5: Double, par7: Double) = this.proxy.getClosestVulnerablePlayer(par1, par3, par5, par7)
  override def getPlayerEntityByName(par1Str: String) = this.proxy.getPlayerEntityByName(par1Str)
  override def sendQuittingDisconnectingPacket() = this.proxy.sendQuittingDisconnectingPacket()
  override def checkSessionLock() = this.proxy.checkSessionLock()
  override def func_82738_a(par1: Long) = this.proxy.func_82738_a(par1)
  override def getSeed = this.proxy.getSeed
  override def getTotalWorldTime = this.proxy.getTotalWorldTime
  override def getWorldTime = this.proxy.getWorldTime
  override def setWorldTime(par1: Long) = this.proxy.setWorldTime(par1)
  override def getSpawnPoint = this.proxy.getSpawnPoint
  override def setSpawnLocation(par1: Int, par2: Int, par3: Int) = this.proxy.setSpawnLocation(par1, par2, par3)
  override def joinEntityInSurroundings(par1Entity: Entity) = this.proxy.joinEntityInSurroundings(par1Entity)
  override def canMineBlock(par1EntityPlayer: EntityPlayer, par2: Int, par3: Int, par4: Int) = this.proxy.canMineBlock(par1EntityPlayer, par2, par3, par4)
  override def setEntityState(par1Entity: Entity, par2: Byte) = this.proxy.setEntityState(par1Entity, par2)
  override def getChunkProvider = this.proxy.getChunkProvider
  override def addBlockEvent(x: Int, y: Int, z: Int, block: Block, event: Int, data: Int) = this.proxy.addBlockEvent(x, y, z, block, event, data)
  override def getSaveHandler = this.proxy.getSaveHandler
  override def getGameRules = this.proxy.getGameRules
  override def updateAllPlayersSleepingFlag() = this.proxy.updateAllPlayersSleepingFlag()
  override def getWeightedThunderStrength(par1: Float) = this.proxy.getWeightedThunderStrength(par1)
  override def setThunderStrength(thunderStrength: Float) = this.proxy.setThunderStrength(thunderStrength)
  override def getRainStrength(par1: Float) = this.proxy.getRainStrength(par1)
  override def setRainStrength(par1: Float) = this.proxy.setRainStrength(par1)
  override def isThundering = this.proxy.isThundering
  override def isRaining = this.proxy.isRaining
  override def canLightningStrikeAt(par1: Int, par2: Int, par3: Int) = this.proxy.canLightningStrikeAt(par1, par2, par3)
  override def isBlockHighHumidity(par1: Int, par2: Int, par3: Int) = this.proxy.isBlockHighHumidity(par1, par2, par3)
  override def setItemData(par1Str: String, par2WorldSavedData: WorldSavedData) = this.proxy.setItemData(par1Str, par2WorldSavedData)
  override def loadItemData(par1Class: Class[_], par2Str: String) = this.proxy.loadItemData(par1Class, par2Str)
  override def getUniqueDataId(par1Str: String) = this.proxy.getUniqueDataId(par1Str)
  override def playBroadcastSound(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int) = this.proxy.playBroadcastSound(par1, par2, par3, par4, par5)
  override def playAuxSFX(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int) = this.proxy.playAuxSFX(par1, par2, par3, par4, par5)
  override def playAuxSFXAtEntity(par1EntityPlayer: EntityPlayer, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) = this.proxy.playAuxSFXAtEntity(par1EntityPlayer, par2, par3, par4, par5, par6)
  override def getHeight = this.proxy.getHeight
  override def getActualHeight = this.proxy.getActualHeight
  override def setRandomSeed(par1: Int, par2: Int, par3: Int) = this.proxy.setRandomSeed(par1, par2, par3)
  override def findClosestStructure(name: String, x: Int, y: Int, z: Int) = this.proxy.findClosestStructure(name, x, y, z)
  override def extendedLevelsInChunkCache = this.proxy.extendedLevelsInChunkCache
  override def getHorizon = this.proxy.getHorizon
  override def addWorldInfoToCrashReport(par1CrashReport: CrashReport) = this.proxy.addWorldInfoToCrashReport(par1CrashReport)
  override def destroyBlockInWorldPartially(x: Int, y: Int, z: Int, i: Int, i1: Int) = this.proxy.destroyBlockInWorldPartially(x, y, z, i, i1)
  override def getWorldVec3Pool = this.proxy.getWorldVec3Pool
  override def getCurrentDate = this.proxy.getCurrentDate
  override def makeFireworks(par1: Double, par3: Double, par5: Double, par7: Double, par9: Double, par11: Double, par13nbtTagCompound: NBTTagCompound) = this.proxy.makeFireworks(par1, par3, par5, par7, par9, par11, par13nbtTagCompound)
  override def getScoreboard: Scoreboard = this.proxy.getScoreboard
  override def func_147453_f(x: Int, y: Int, z: Int, block: Block) = this.proxy.func_147453_f(x, y, z, block)
  override def func_147462_b(x: Double, y: Double, z: Double) = this.proxy.func_147462_b(x, y, z)
  override def func_147473_B(x: Int, y: Int, z: Int) = this.proxy.func_147473_B(x, y, z)
  override def func_147450_X() = this.proxy.func_147450_X()
}
