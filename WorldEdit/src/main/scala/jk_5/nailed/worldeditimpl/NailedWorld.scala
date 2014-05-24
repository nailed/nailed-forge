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
import net.minecraft.util.LongHashMap
import net.minecraft.world.biome.BiomeGenBase
import net.minecraft.world.gen.feature._
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}
import scala.ref.WeakReference
import scala.collection.JavaConversions._
import net.minecraft.enchantment.Enchantment
import net.minecraft.block.Block
import java.util
import com.sk89q.worldedit.util.TreeGenerator
import java.util.Random
import com.sk89q.worldedit.regions.Region
import scala.Some
import cpw.mods.fml.common.ObfuscationReflectionHelper
import jk_5.nailed.worldeditimpl.undo.UndoWorldProxy

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

  private def isEntityOfType(entity: Entity, typ: EntityType): Boolean = typ match {
    case EntityType.ALL =>
      entity.isInstanceOf[EntityBoat] || entity.isInstanceOf[EntityItem] || entity.isInstanceOf[EntityFallingBlock] || entity.isInstanceOf[EntityMinecart] || entity.isInstanceOf[EntityHanging] || entity.isInstanceOf[EntityTNTPrimed] || entity.isInstanceOf[EntityXPOrb] || entity.isInstanceOf[EntityEnderEye] || entity.isInstanceOf[IProjectile]
    case EntityType.PROJECTILES | EntityType.ARROWS =>
      entity.isInstanceOf[EntityEnderEye] || entity.isInstanceOf[IProjectile]
    case EntityType.BOATS =>
      entity.isInstanceOf[EntityBoat]
    case EntityType.ITEMS =>
      entity.isInstanceOf[EntityItem]
    case EntityType.FALLING_BLOCKS =>
      entity.isInstanceOf[EntityFallingBlock]
    case EntityType.MINECARTS =>
      entity.isInstanceOf[EntityMinecart]
    case EntityType.PAINTINGS =>
      entity.isInstanceOf[EntityPainting]
    case EntityType.ITEM_FRAMES =>
      entity.isInstanceOf[EntityItemFrame]
    case EntityType.TNT =>
      entity.isInstanceOf[EntityTNTPrimed]
    case EntityType.XP_ORBS =>
      entity.isInstanceOf[EntityXPOrb]
    case _ => false
  }

  private def getTreeGeneratorByType(typ: TreeGenerator.TreeType, rand: Random): WorldGenerator = typ match {
    case TreeGenerator.TreeType.ACACIA =>
      new WorldGenSavannaTree(true)
    case TreeGenerator.TreeType.BIG_TREE =>
      new WorldGenBigTree(true)
    case TreeGenerator.TreeType.BIRCH =>
      new WorldGenForest(true, false)
    case TreeGenerator.TreeType.BROWN_MUSHROOM =>
      new WorldGenBigMushroom(0)
    case TreeGenerator.TreeType.DARK_OAK =>
      new WorldGenCanopyTree(true)
    case TreeGenerator.TreeType.JUNGLE =>
      new WorldGenMegaJungle(true, 10, 20, 3, 3)
    case TreeGenerator.TreeType.JUNGLE_BUSH =>
      new WorldGenShrub(3, 0)
    case TreeGenerator.TreeType.MEGA_REDWOOD =>
      new WorldGenMegaPineTree(true, rand.nextBoolean)
    case TreeGenerator.TreeType.REDWOOD =>
      new WorldGenTaiga2(true)
    case TreeGenerator.TreeType.RED_MUSHROOM =>
      new WorldGenBigMushroom(1)
    case TreeGenerator.TreeType.SMALL_JUNGLE =>
      new WorldGenTrees(true, 4 + rand.nextInt(7), 3, 3, false)
    case TreeGenerator.TreeType.SWAMP =>
      new WorldGenSwamp
    case TreeGenerator.TreeType.TALL_BIRCH =>
      new WorldGenForest(true, true)
    case TreeGenerator.TreeType.TALL_REDWOOD =>
      new WorldGenTaiga1
    case TreeGenerator.TreeType.TREE =>
      new WorldGenTrees(true)
    case _ => null
  }

  @inline def getBaseBlock(block: Block) = new BaseBlock(net.minecraft.block.Block.getIdFromBlock(block))
  @inline def getBaseBlock(block: Block, metaData: Int) = new BaseBlock(net.minecraft.block.Block.getIdFromBlock(block), metaData)
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
          if (distanceTo <= sqRadius && NailedWorld.isEntityOfType(e, typ)) {
            e.isDead = true
            count += 1
          }
        case _ =>
      }
      count
    case _ => 0
  }

  def regenerate(region: Region, editSession: EditSession): Boolean = this.world.get match {
    case Some(theWorld) =>
      var result = false
      region.getChunks.foreach(chunk => {
        val chunkCoords = new Vector(chunk.getBlockX * 16, 0, chunk.getBlockZ * 16)
        val chunkData = this.getChunkData(chunkCoords, editSession)
        if(this.regenChunk(chunk, theWorld)){
          result = true
          this.applyChanges(chunkCoords, chunkData, editSession, region, theWorld)
        }else{
          result = false
        }
      })
      result
    case _ => false
  }

  def getChunkData(chunkCoords: Vector, session: EditSession): Array[BaseBlock] = {
    val chunkData: Array[BaseBlock] = new Array[BaseBlock](16 * 16 * (this.getMaxY + 1))
    for(x <- 0 until 16){
      for(y <- 0 until this.getMaxY){
        for(z <- 0 until 16){
          val pt = chunkCoords.add(x, y, z)
          val index = y * 16 * 16 + z * 16 + x
          chunkData(index) = session.getBlock(pt)
        }
      }
    }
    chunkData
  }

  def regenChunk(chunkCoords: Vector2D, world: World): Boolean = {
    try{
      val provider = world.getChunkProvider
      if(!provider.isInstanceOf[ChunkProviderServer]) return false
      val chunkServer = provider.asInstanceOf[ChunkProviderServer]
      val cls = classOf[ChunkProviderServer]

      val chunksToUnload: util.Set[_] = ObfuscationReflectionHelper.getPrivateValue(cls, chunkServer, "chunksToUnload", "field_73248_b")
      val loadedChunkHashMap: LongHashMap = ObfuscationReflectionHelper.getPrivateValue(cls, chunkServer, "loadedChunkHashMap", "field_73244_f")
      val loadedChunks: util.List[Chunk] = ObfuscationReflectionHelper.getPrivateValue(cls, chunkServer, "loadedChunks", "field_73245_g")
      val chunkProvider: IChunkProvider = ObfuscationReflectionHelper.getPrivateValue(cls, chunkServer, "currentChunkProvider", "field_73246_d")

      val chunkCoordX = chunkCoords.getBlockX
      val chunkCoordZ = chunkCoords.getBlockZ

      if(chunkServer.chunkExists(chunkCoordX, chunkCoordZ)){
        chunkServer.loadChunk(chunkCoordX, chunkCoordZ).onChunkUnload()
      }
      val chunkIndex = ChunkCoordIntPair.chunkXZ2Int(chunkCoordX, chunkCoordZ)
      chunksToUnload.remove(chunkIndex)
      loadedChunkHashMap.remove(chunkIndex)

      val chunk = chunkProvider.provideChunk(chunkCoordX, chunkCoordZ)
      loadedChunkHashMap.add(chunkIndex, chunk)
      loadedChunks.add(chunk)
      if(chunk != null){
        try{
          chunk.onChunkLoad()
          chunk.populateChunk(chunkProvider, chunkProvider, chunkCoordX, chunkCoordZ)
        }catch{
          case ex: Exception =>
            for(biome <- BiomeGenBase.getBiomeGenArray){
              if(biome != null && biome.theBiomeDecorator != null){
                biome.theBiomeDecorator.currentWorld = null
              }
            }
        }
        try{
          chunk.func_150809_p()
        }catch{
          case ex: Exception =>
            ex.printStackTrace(System.out)
        }
      }
    }catch{
      case th: Throwable =>
        th.printStackTrace()
        return false
    }
    true
  }

  def applyChanges(chunkCoords: Vector, oldChunkData: Array[BaseBlock], editSession: EditSession, region: Region, world: World){
    val playerManager = world match {
      case server: WorldServer =>
        Some(server.getPlayerManager)
      case _ => None
    }
    for(x <- 0 until 16){
      for(y <- 0 until this.getMaxY){
        for(z <- 0 until 16){
          val pt = chunkCoords.add(x, y, z)
          val index = y * 16 * 16 + z * 16 + x
          if(!region.contains(pt)){
            editSession.smartSetBlock(pt, oldChunkData(index))
          }else{
            playerManager match {
              case Some(man) => man.markBlockForUpdate(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
              case None =>
            }
            editSession.rememberChange(pt, oldChunkData(index), editSession.rawGetBlock(pt))
          }
        }
      }
    }
  }

  def setBiome(pt: Vector2D, biome: BiomeType): Unit = this.world.get match {
    case Some(theWorld) =>
        val biomeGen = NailedBiomeTypes.getFromBiomeType(biome.asInstanceOf[NailedBiomeType])
        if(biomeGen == null) return
        val biomeID = biomeGen.biomeID.toByte
        if(theWorld.getChunkProvider.chunkExists(pt.getBlockX >> 4, pt.getBlockZ >> 4)){
          val chunk = theWorld.getChunkFromBlockCoords(pt.getBlockX, pt.getBlockZ)
          if(chunk != null && chunk.isChunkLoaded){
            val biomes = chunk.getBiomeArray
            biomes((pt.getBlockZ & 0xF) << 4 | pt.getBlockX & 0xF) = biomeID
          }
        }
    case _ =>
  }


  override def setBlock(pt: Vector, block: foundation.Block, notifyAdjacent: Boolean): Boolean = this.world.get match {
    case Some(theWorld) =>
      val newBlock = NailedWorld.getBlockById(block.getId)
      val result = theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, newBlock)
      if(result && block.isInstanceOf[TileEntityBlock]){
        this.copyToWorld(pt, block.asInstanceOf[BaseBlock])
      }
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, block.getData, 2)
      result
    case _ => false
  }

  def setBlockData(pt: Vector, data: Int): Unit = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, data, 3)
    case _ =>
  }

  def setBlockDataFast(pt: Vector, data: Int): Unit = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlockMetadataWithNotify(pt.getBlockX, pt.getBlockY, pt.getBlockZ, data, 3)
    case _ =>
  }

  def setBlockType(pt: Vector, typ: Int): Boolean = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, NailedWorld.getBlockById(typ), 0, 3)
    case _ => false
  }

  override def setBlockTypeFast(pt: Vector, typ: Int): Boolean = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, NailedWorld.getBlockById(typ))
    case _ => false
  }

  override def setTypeIdAndData(pt: Vector, typ: Int, data: Int): Boolean = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, NailedWorld.getBlockById(typ), data, 3)
    case _ => false
  }

  override def setTypeIdAndDataFast(pt: Vector, typ: Int, data: Int): Boolean = this.world.get match {
    case Some(theWorld) =>
      theWorld.setBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ, NailedWorld.getBlockById(typ), data, 3)
    case _ => false
  }

  override def generateTree(typ: TreeGenerator.TreeType, editSession: EditSession, pt: Vector): Boolean = this.world.get match {
    case Some(theWorld) =>
      val generator = NailedWorld.getTreeGeneratorByType(typ, theWorld.rand)
      if(generator != null){
        generator.generate(new UndoWorldProxy(editSession, theWorld), theWorld.rand, pt.getBlockX, pt.getBlockY, pt.getBlockZ)
      }else false
    case _ => false
  }

  private def getBlockAt(pt: Vector): Block = this.world.get match {
    case Some(theWorld) => theWorld.getBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
    case _ => Blocks.air
  }

  override def equals(other: Any): Boolean = other match {
    case w: NailedWorld =>
      val world = w.world.get
      if(world != null){
        world == this.world.get
      }else{
        this.world.get == null
      }
    case _ => false
  }

  override def hashCode = if(this.world.get != null) this.world.get.hashCode else 0
}
