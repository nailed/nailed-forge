package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketMinMax extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(2, 2);
    }

    @Override
    public void process() throws PacketException {
        WERenderer.selection().get().setMinMax(this.getIntArgument(0), this.getIntArgument(1));
    }
}
