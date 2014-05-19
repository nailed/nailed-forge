package jk_5.nailed.worldeditimpl.impl

import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.Vector
import net.minecraft.tileentity.TileEntity
import net.minecraft.nbt.NBTTagCompound

/**
 * No description given
 *
 * @author jk-5
 */
class NailedTileEntityBlock(typ: Int, data: Int, var tile: TileEntity) extends BaseBlock(typ, data) {

  override def getNbtId: String = {
    val tag = new NBTTagCompound
    try{
      this.tile.writeToNBT(tag)
      tag.getString("id")
    }catch{
      case e: Exception => ""
    }
  }

  def createCopyAt(pt: Vector): TileEntity = try{
    val cl = this.tile.getClass
    val newTile = cl.newInstance()
    newTile.readFromNBT(this.getTileEntityData(pt))
    newTile
  }catch{
    case e: Exception => null
  }

  private def getTileEntityData(pt: Vector): NBTTagCompound = {
    val tag = new NBTTagCompound
    this.tile.writeToNBT(tag)
    tag.setInteger("x", pt.getBlockX)
    tag.setInteger("y", pt.getBlockY)
    tag.setInteger("z", pt.getBlockZ)
    tag
  }
}
