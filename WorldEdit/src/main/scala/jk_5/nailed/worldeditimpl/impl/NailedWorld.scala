package jk_5.nailed.worldeditimpl.impl

import com.sk89q.worldedit._
import com.sk89q.worldedit.blocks._
import com.sk89q.worldedit.regions.Region
import net.minecraft.world.World
import scala.ref.WeakReference
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity._
import scala.Some
import scala.collection.JavaConversions._
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.enchantment.Enchantment
import net.minecraft.block.Block
import net.minecraft.init.Blocks

/**
 * No description given
 *
 * @author jk-5
 */
object NailedWorld {
  def apply(world: World): NailedWorld = new NailedWorld(WeakReference(world))

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

class NailedWorld private (world: WeakReference[World]) extends LocalWorld {

  override def getName = world.get.get.provider.getDimensionName

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
    case Some(theWorld) =>
      NailedBiomeTypes.getFromBaseBiome(theWorld.getBiomeGenForCoords(pt.getBlockX, pt.getBlockZ))
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
      //val killPets = (flags & KillFlags.PETS) == KillFlags.PETS
      count
    case _ => 0
  }



  override def regenerate(p1: Region, p2: EditSession): Boolean = ???

  override def setBlockType(p1: Vector, p2: Int): Boolean = ???

  override def setBiome(p1: Vector2D, p2: BiomeType): Unit = ???

  override def removeEntities(p1: EntityType, p2: Vector, p3: Int): Int = ???

  override def setBlockData(p1: Vector, p2: Int): Unit = ???

  override def setBlockDataFast(p1: Vector, p2: Int): Unit = ???

  private def getBlockAt(pt: Vector): Block = this.world.get match {
    case Some(theWorld) => theWorld.getBlock(pt.getBlockX, pt.getBlockY, pt.getBlockZ)
    case _ => Blocks.air
  }
}
