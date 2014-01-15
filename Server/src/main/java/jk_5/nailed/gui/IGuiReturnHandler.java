package jk_5.nailed.gui;

import io.netty.buffer.ByteBuf;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiReturnHandler {

    public void readGuiCloseData(ByteBuf buffer);
}
