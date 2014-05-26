package jk_5.worldeditcui.network.packet;

import jk_5.worldeditcui.render.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketEllipsoid extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(4, 4);
    }

    @Override
    public void process() throws PacketException {
        int id = this.getIntArgument(0);
        if (id == 0) {
            int x = this.getIntArgument(1);
            int y = this.getIntArgument(2);
            int z = this.getIntArgument(3);
            WERenderer.selection().get().setEllipsoidCenter(x, y, z);
        } else if (id == 1) {
            double x = this.getDoubleArgument(1);
            double y = this.getDoubleArgument(2);
            double z = this.getDoubleArgument(3);
            WERenderer.selection().get().setEllipsoidRadii(x, y, z);
        }
    }
}
