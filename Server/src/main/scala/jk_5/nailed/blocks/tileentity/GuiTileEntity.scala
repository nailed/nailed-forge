package jk_5.nailed.blocks.tileentity

import io.netty.buffer.ByteBuf
import jk_5.nailed.api.player.Player

/**
 * No description given
 *
 * @author jk-5
 */
trait GuiTileEntity {

  def canPlayerOpenGui(player: Player): Boolean
  def writeGuiData(buffer: ByteBuf)
}
