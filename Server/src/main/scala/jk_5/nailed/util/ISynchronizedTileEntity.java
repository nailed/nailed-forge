package jk_5.nailed.util;

import io.netty.buffer.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ISynchronizedTileEntity {

    void writeData(ByteBuf buffer);
}
