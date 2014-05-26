package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPoint2D extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(4, 5);
    }

    @Override
    public void process() throws PacketException {
        int id = this.getIntArgument(0);
        int x = this.getIntArgument(1);
        int y = this.getIntArgument(2);
        WERenderer.selection().get().setPolygonPoint(id, x, y);
    }
}
