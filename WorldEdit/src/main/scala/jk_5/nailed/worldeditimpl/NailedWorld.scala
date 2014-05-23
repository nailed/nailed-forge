package jk_5.nailed.worldeditimpl

import com.sk89q.worldedit._
import net.minecraft.world.{WorldServer, ChunkCoordIntPair, World}
import net.minecraft.tileentity._
import net.minecraft.inventory.IInventory
import com.sk89q.worldedit.blocks._
import net.minecraft.entity.item._
import net.minecraft.entity.{Entity, IProjectile, EntityHanging, EntityLiving}
import net.minecraft.entity.passive.{EntityAmbientCreature, EntityVillager, EntityTameable, EntityAnimal}
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.gen.ChunkProviderServer
import com.mumfrey.worldeditwrapper.reflect.PrivateFields
import net.minecraft.util.LongHashMap
import net.minecraft.world.biome.BiomeGenBase
import net.minecraft.server.management.PlayerManager
import net.minecraft.world.gen.feature._
import com.mumfrey.worldeditwrapper.impl.undo.UndoWorldProxy
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}
import scala.ref.WeakReference
import scala.collection.JavaConversions._
import net.minecraft.enchantment.Enchantment
import net.minecraft.block.Block
import java.util
import scala.Some

/**
 * No description given
 *
 * @author jk-5
 */
object NailedWorld {
  def apply(world: World) = new NailedWorld(new WeakReference[World](world))

  def getItemStack(item: BaseItemStack): ItemStack = {
    val is = new ItemStack(Item.getItemById(item.getType), item.getAmount, item.getData)
    item.getEnchantments.entrySet().foreach(e =>
      is.addEnchantment(Enchantment.enchantmentsList(e.getKey.intValue()), e.getValue.intValue())
    )
    is
  }

  @inline def getIdFromBlock(block: Block) = if(block == null) 0 else Block.getIdFromBlock(block)

  @inline def getBlockById(id: Int): Block = {
    val bl = Block.getBlockById(id)
    if(bl == null) Blocks.air else bl
  }
}
class NailedWorld private (val world: WeakReference[World]) extends LocalWorld {

  def getName = this.world.get.get.provider.getDimensionName

  override def clearContainerBlockContents(pt: Vector): Boolean = this.world.get match {
    case Some(theWorld) =>
      val tile = theWorld.getTileEntity(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
      tile match {
        case inv: IInventory =>
          for(i <- 0 until inv.getSizeInventory){
            inv.setInventorySlotContents(i, null)
          }
          true
        case _ => false
      }
    case _ => false
  }

  override def copyFromWorld(pt: Vector, block: BaseBlock): Boolean = this.world.get match {
    case Some(theWorld) =>
      block match {
        case t: NailedTileEntityBlock =>
          t.tile = theWorld.getTileEntity(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
          true
        case _ => false
      }
    case _ => false
  }

  override def copyToWorld(pt: Vector, block: BaseBlock): Boolean = this.world.get match {
    case Some(theWorld) =>
      val newTile = createCopyAt(pt, block)
      if(newTile != null){
        theWorld.removeTileEntity(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
        theWorld.setTileEntity(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newTile)
        true
      }else false
    case _ => false
  }

  def createCopyAt(pt: Vector, block: BaseBlock): TileEntity = block match {
    case b: SignBlock =>
      val tile = new TileEntitySign
      tile.signText = b.getText
      tile
    case b: MobSpawnerBlock =>
      val tile = new TileEntityMobSpawner
      tile.func_145881_a().setEntityName(b.getMobType)
      tile
    case b: NoteBlock =>
      val tile = new TileEntityNote
      tile.note = b.getNote
      tile
    case b: SkullBlock =>
      val tile = new TileEntitySkull
      tile.func_145905_a(b.getSkullType, b.getOwner)
      tile.func_145903_a(b.getRot)
      tile
    case b: NailedTileEntityBlock =>
      b.createCopyAt(pt)
    case _ => null
  }

  override def dropItem(pos: Vector, item: BaseItemStack){
    if(item == null || item.getType == 0) return
    this.world.get match {
      case Some(theWorld) =>
        val entity = new EntityItem(theWorld, pos.getX, pos.getY, pos.getZ, NailedWorld.getItemStack(item))
        entity.delayBeforeCanPickup = 10
        theWorld.spawnEntityInWorld(entity)
      case _ =>
    }
  }

  override def getBiome(pt: Vector2D): BiomeType = this.world.get match {
    case Some(theWorld) => NailedBiomeTypes.getFromBaseBiome(theWorld.getBiomeGenForCoords(pt.getBlockX, pt.getBlockZ))
    case _ => BiomeType.UNKNOWN
  }

  override def getBlock(pt: Vector): BaseBlock = this.world.get match {
    case Some(theWorld) =>
      val typ = this.getBlockType(pt)
      val data = this.getBlockData(pt)
      val tile = theWorld.getTileEntity(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
      if(tile != null){
        val tb = new NailedTileEntityBlock(typ, data, tile)
        this.copyFromWorld(pt, tb)
        tb
      }else new BaseBlock(typ, data)
    case _ => new BaseBlock(0, 0)
  }

  override def getBlockData(p1: Vector): Int = this.world.get match {
    case Some(theWorld) => theWorld.getBlockMetadata(p1.getBlockX, p1.getBlockY, p1.getBlockZ)
    case _ => 0
  }

  override def getBlockLightLevel(p1: Vector): Int = this.world.get match {
    case Some(theWorld) => theWorld.getBlockLightValue(p1.getBlockX, p1.getBlockY, p1.getBlockZ)
    case _ => 0
  }

  override def getBlockType(pt: Vector): Int = {
    NailedWorld.getIdFromBlock(this.getBlockAt(pt))
  }

  override def isValidBlockType(typ: Int) = typ == 0 || NailedWorld.getBlockById(typ) != null

  override def killMobs(origin: Vector, radius: Double, flags: Int): Int = this.world.get match {
    case Some(theWorld) =>
      var count = 0
      val killPets = (flags & KillFlags.PETS) == KillFlags.PETS
      val killNPCs = (flags & KillFlags.NPCS) == KillFlags.NPCS
      val killAnimals = (flags & KillFlags.ANIMALS) == KillFlags.ANIMALS
      val killGolems = (flags & KillFlags.GOLEMS) == KillFlags.GOLEMS
      val killAmbient = (flags & KillFlags.AMBIENT) == KillFlags.AMBIENT
      val sqRadius = radius * radius
      for(entity <- theWorld.loadedEntityList.asInstanceOf[util.List[Entity]]){
        if(entity.isInstanceOf[EntityLiving]){
          val distanceTo: Double = if(radius < 0) 0 else origin.distanceSq(new Vector(entity.posX, entity.posY, entity.posZ))
          if(distanceTo <= sqRadius){
            if((killAnimals && entity.isInstanceOf[EntityAnimal]) || (killPets && entity.isInstanceOf[EntityTameable] && entity.asInstanceOf[EntityTameable].isTamed) || (killGolems && entity.isInstanceOf[EntityGolem]) || (killNPCs && entity.isInstanceOf[EntityVillager]) || (killAmbient && entity.isInstanceOf[EntityAmbientCreature])){
              entity.setDead()
              count += 1
            }
          }
        }
      }
      count
    case _ => 0
  }

  def removeEntities(typ: EntityType, origin: Vector, radius: Int): Int = this.world.get match {
    case Some(theWorld) =>
      var count = 0
      val sqRadius = radius * radius
      theWorld.loadedEntityList.asInstanceOf[util.List[Entity]].foreach {
        case e: EntityLiving =>
          val distanceTo = if(radius < 0) 0 else origin.distanceSq(new Vector(e.posX, e.posY, e.posZ))
          if (distanceTo <= sqRadius && NailedWorld.isEntityOfType(entity, typ)) {
            e.isDead = true
            count += 1
          }
        case _ =>
      }
    case _ => 0
  }

  def regenerate(region: Region, editSession: EditSession): Boolean = {
    var result: Boolean = false
    val chunks: Set[Vector2D] = region.getChunks
    for (chunk <- chunks) {
      val theWorld: World = this.world.get
      if (theWorld != null) {
        val chunkCoords: Vector = new Vector(chunk.getBlockX * 16, 0, chunk.getBlockZ * 16)
        val chunkData: Array[BaseBlock] = this.getChunkData(chunkCoords, editSession)
        if (this.regenChunk(chunk, theWorld)) {
          result = true
          this.applyChanges(chunkCoords, chunkData, editSession, region, theWorld)
        }
        else {
          result = false
        }
      }
    }
    return result
  }

  def getChunkData(chunkCoords: Vector, session: EditSession): Array[BaseBlock] = {
    val chunkData: Array[BaseBlock] = new Array[BaseBlock](16 * 16 * (this.getMaxY + 1))
    {
      var x: Int = 0
      while (x < 16) {
        {
          {
            var y: Int = 0
            while (y <= this.getMaxY) {
              {
                {
                  var z: Int = 0
                  while (z < 16) {
                    {
                      val pt: Vector = chunkCoords.add(x, y, z)
                      val index: Int = y * 16 * 16 + z * 16 + x
                      chunkData(index) = session.getBlock(pt)
                    }
                    ({
                      z += 1; z - 1
                    })
                  }
                }
              }
              ({
                y += 1; y - 1
              })
            }
          }
        }
        ({
          x += 1; x - 1
        })
      }
    }
    return chunkData
  }

  def regenChunk(chunkCoords: Vector2D, world: World): Boolean = {
    try {
      val provider: IChunkProvider = world.getChunkProvider
      if (!(provider.isInstanceOf[ChunkProviderServer])) {
        return false
      }
      val chunkServer: ChunkProviderServer = provider.asInstanceOf[ChunkProviderServer]
      val chunksToUnload: Set[_] = PrivateFields.chunksToUnload.get(chunkServer)
      val loadedChunkHashMap: LongHashMap = PrivateFields.loadedChunkHashMap.get(chunkServer)
      val loadedChunks: List[Chunk] = PrivateFields.loadedChunks.get(chunkServer)
      val chunkProvider: IChunkProvider = PrivateFields.currentChunkProvider.get(chunkServer)
      val chunkCoordX: Int = chunkCoords.getBlockX
      val chunkCoordZ: Int = chunkCoords.getBlockZ
      if (chunkServer.chunkExists(chunkCoordX, chunkCoordZ)) {
        chunkServer.loadChunk(chunkCoordX, chunkCoordZ).onChunkUnload
      }
      val chunkIndex: Long = ChunkCoordIntPair.chunkXZ2Int(chunkCoordX, chunkCoordZ)
      chunksToUnload.remove(chunkIndex)
      loadedChunkHashMap.remove(chunkIndex)
      val chunk: Chunk = chunkProvider.provideChunk(chunkCoordX, chunkCoordZ)
      loadedChunkHashMap.add(chunkIndex, chunk)
      loadedChunks.add(chunk)
      if (chunk != null) {
        try {
          chunk.onChunkLoad
          chunk.populateChunk(chunkProvider, chunkProvider, chunkCoordX, chunkCoordZ)
        }
        catch {
          case ex: Exception => {
            for (biome <- BiomeGenBase.getBiomeGenArray) {
              if (biome != null && biome.theBiomeDecorator != null) {
                biome.theBiomeDecorator.currentWorld = null
              }
            }
          }
        }
        try {
          chunk.func_150809_p
        }
        catch {
          case ex: Exception => {
            ex.printStackTrace(System.out)
          }
        }
      }
    }
    catch {
      case th: Throwable => {
        th.printStackTrace
        return false
      }
    }
    return true
  }

  def applyChanges(chunkCoords: Vector, oldChunkData: Array[BaseBlock], editSession: EditSession, region: Region, world: World) {
    var playerManager: PlayerManager = null
    if (world.isInstanceOf[WorldServer]) {
      playerManager = (world.asInstanceOf[WorldServer]).getPlayerManager
    }
    {
      var x: Int = 0
      while (x < 16) {
        {
          {
            var y: Int = 0
            while (y <= this.getMaxY) {
              {
                {
                  var z: Int = 0
                  while (z < 16) {
                    {
                      val pt: Vector = chunkCoords.add(x, y, z)
                      val index: Int = y * 16 * 16 + z * 16 + x
                      if (!region.contains(pt)) {
                        editSession.smartSetBlock(pt, oldChunkData(index))
                      }
                      else {
                        if (playerManager != null) {
                          playerManager.markBlockForUpdate(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
                        }
                        editSession.rememberChange(pt, oldChunkData(index), editSession.rawGetBlock(pt))
                      }
                    }
                    ({
                      z += 1; z - 1
                    })
                  }
                }
              }
              ({
                y += 1; y - 1
              })
            }
          }
        }
        ({
          x += 1; x - 1
        })
      }
    }
  }

  def setBiome(pt: Vector2D, biome: BiomeType) {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val biomeGen: BiomeGenBase = NailedBiomeTypes.getFromBiomeType(biome.asInstanceOf[NailedBiomeType])
      if (biomeGen == null) {
        return
      }
      val biomeID: Byte = biomeGen.biomeID.asInstanceOf[Byte]
      if (theWorld.getChunkProvider.chunkExists(pt.getBlockX >> 4, pt.getBlockZ >> 4)) {
        val chunk: Chunk = theWorld.getChunkFromBlockCoords(pt.getBlockX, pt.getBlockZ)
        if ((chunk != null) && (chunk.isChunkLoaded)) {
          val biomes: Array[Byte] = chunk.getBiomeArray
          biomes((pt.getBlockZ & 0xF) << 4 | pt.getBlockX & 0xF) = biomeID
        }
      }
    }
  }

  @SuppressWarnings(Array("cast")) override def setBlock(pt: Vector, block: Block, notify: Boolean): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null && block.isInstanceOf[BaseBlock]) {
      val newBlock: Block = VanillaWorld.getBlockById(block.getId)
      val result: Boolean = theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock)
      if (result && block.isInstanceOf[TileEntityBlock]) {
        this.copyToWorld(pt, block.asInstanceOf[BaseBlock])
      }
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, block.getData, 2)
      return result
    }
    return false
  }

  def setBlockData(pt: Vector, data: Int) {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, data, 3)
    }
  }

  def setBlockDataFast(pt: Vector, data: Int) {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, data, 3)
    }
  }

  def setBlockType(pt: Vector, `type`: Int): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val newBlock: Block = VanillaWorld.getBlockById(`type`)
      return theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock, 0, 3)
    }
    return false
  }

  override def setBlockTypeFast(pt: Vector, `type`: Int): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val newBlock: Block = VanillaWorld.getBlockById(`type`)
      return theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock)
    }
    return false
  }

  override def setTypeIdAndData(pt: Vector, `type`: Int, data: Int): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val newBlock: Block = VanillaWorld.getBlockById(`type`)
      return theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock, data, 3)
    }
    return false
  }

  override def setTypeIdAndDataFast(pt: Vector, `type`: Int, data: Int): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val newBlock: Block = VanillaWorld.getBlockById(`type`)
      return theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock, data, 3)
    }
    return false
  }

  override def generateTree(`type`: TreeGenerator.TreeType, editSession: EditSession, pt: Vector): Boolean = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      val treeGenerator: WorldGenerator = VanillaWorld.getTreeGeneratorByType(`type`, theWorld.rand)
      if (treeGenerator != null) {
        val result: Boolean = treeGenerator.generate(new UndoWorldProxy(editSession, theWorld), theWorld.rand, pt.getBlockX, pt.getBlockY, pt.getBlockZ)
        return result
      }
    }
    return false
  }

  @SuppressWarnings(Array("deprecation")) def getBaseBlock(block: Block): BaseBlock = {
    val `type`: Int = net.minecraft.block.Block.getIdFromBlock(block)
    return new BaseBlock(`type`)
  }

  @SuppressWarnings(Array("deprecation")) def getBaseBlock(block: Block, metaData: Int): BaseBlock = {
    val `type`: Int = net.minecraft.block.Block.getIdFromBlock(block)
    return new BaseBlock(`type`, metaData)
  }

  private def getBlockAt(pt: Vector): Block = {
    val theWorld: World = this.world.get
    if (theWorld != null) {
      return theWorld.getBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
    }
    return Blocks.air
  }

  @SuppressWarnings(Array("deprecation")) def getBlockById(`type`: Int): Block = {
    val block: Block = net.minecraft.block.Block.getBlockById(`type`)
    return if (block != null) block else Blocks.air
  }

  @SuppressWarnings(Array("deprecation")) private def getIdFromBlock(block: Block): Int = {
    return if (block == null) 0 else net.minecraft.block.Block.getIdFromBlock(block)
  }

  def equals(other: AnyRef): Boolean = {
    if (other.isInstanceOf[VanillaWorld]) {
      val otherWorld: World = (other.asInstanceOf[VanillaWorld]).world.get
      return if (otherWorld != null) (otherWorld == this.world.get) else this.world.get == null
    }
    return false
  }

  def hashCode: Int = {
    return if (this.world.get != null) this.world.get.hashCode else 0
  }

  @SuppressWarnings(Array("deprecation")) private def isEntityOfType(entity: Entity, `type`: EntityType): Boolean = {
    `type` match {
      case ALL =>
        return entity.isInstanceOf[EntityBoat] || entity.isInstanceOf[EntityItem] || entity.isInstanceOf[EntityFallingBlock] || entity.isInstanceOf[EntityMinecart] || entity.isInstanceOf[EntityHanging] || entity.isInstanceOf[EntityTNTPrimed] || entity.isInstanceOf[EntityXPOrb] || entity.isInstanceOf[EntityEnderEye] || entity.isInstanceOf[IProjectile]
      case PROJECTILES =>
      case ARROWS =>
        return entity.isInstanceOf[EntityEnderEye] || entity.isInstanceOf[IProjectile]
      case BOATS =>
        return entity.isInstanceOf[EntityBoat]
      case ITEMS =>
        return entity.isInstanceOf[EntityItem]
      case FALLING_BLOCKS =>
        return entity.isInstanceOf[EntityFallingBlock]
      case MINECARTS =>
        return entity.isInstanceOf[EntityMinecart]
      case PAINTINGS =>
        return entity.isInstanceOf[EntityPainting]
      case ITEM_FRAMES =>
        return entity.isInstanceOf[EntityItemFrame]
      case TNT =>
        return entity.isInstanceOf[EntityTNTPrimed]
      case XP_ORBS =>
        return entity.isInstanceOf[EntityXPOrb]
    }
    return false
  }

  private def getTreeGeneratorByType(`type`: TreeGenerator.TreeType, rand: Random): WorldGenerator = {
    `type` match {
      case ACACIA =>
        return new WorldGenSavannaTree(true)
      case BIG_TREE =>
        return new WorldGenBigTree(true)
      case BIRCH =>
        return new WorldGenForest(true, false)
      case BROWN_MUSHROOM =>
        return new WorldGenBigMushroom(0)
      case DARK_OAK =>
        return new WorldGenCanopyTree(true)
      case JUNGLE =>
        return new WorldGenMegaJungle(true, 10, 20, 3, 3)
      case JUNGLE_BUSH =>
        return new WorldGenShrub(3, 0)
      case MEGA_REDWOOD =>
        return new WorldGenMegaPineTree(true, rand.nextBoolean)
      case REDWOOD =>
        return new WorldGenTaiga2(true)
      case RED_MUSHROOM =>
        return new WorldGenBigMushroom(1)
      case SMALL_JUNGLE =>
        return new WorldGenTrees(true, 4 + rand.nextInt(7), 3, 3, false)
      case SWAMP =>
        return new WorldGenSwamp
      case TALL_BIRCH =>
        return new WorldGenForest(true, true)
      case TALL_REDWOOD =>
        return new WorldGenTaiga1
      case TREE =>
        return new WorldGenTrees(true)
    }
    return null
  }
}
