package jk_5.nailed.gui;

import io.netty.buffer.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiReturnHandler {

    void readGuiCloseData(ByteBuf buffer);
}
