package jk_5.nailed.util

import io.netty.buffer.ByteBuf

/**
 * No description given
 *
 * @author jk-5
 */
trait SynchronizedTileEntity {

  def writeData(buffer: ByteBuf)
}
