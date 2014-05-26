package jk_5.worldeditcui.network.packet;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketUpdate extends Packet {

    @Override
    protected void setupArguments() {
        this.setNumberOfArguments(1, 1);
    }

    @Override
    public void process() throws PacketException {

    }
}
