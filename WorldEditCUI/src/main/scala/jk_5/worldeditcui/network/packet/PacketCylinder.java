package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketCylinder extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(5, 5);
    }

    @Override
    public void process() throws PacketException {
        WERenderer.selection().get().setCylinderCenter(this.getIntArgument(0), this.getIntArgument(1), this.getIntArgument(2));
        WERenderer.selection().get().setCylinderRadius(this.getDoubleArgument(3), this.getDoubleArgument(4));
    }
}
