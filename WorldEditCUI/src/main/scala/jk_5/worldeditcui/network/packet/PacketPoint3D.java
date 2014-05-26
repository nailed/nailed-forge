package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPoint3D extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(5, 6);
    }

    @Override
    public void process() throws PacketException {
        int id = this.getIntArgument(0);
        int x = this.getIntArgument(1);
        int y = this.getIntArgument(2);
        int z = this.getIntArgument(3);
        WERenderer.selection().get().setCuboidPoint(id, x, y, z);
    }
}
