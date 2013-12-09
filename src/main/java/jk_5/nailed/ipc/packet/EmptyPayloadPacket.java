package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class EmptyPayloadPacket extends IpcPacket {

    @Override
    public final void read(JsonObject json) {

    }

    @Override
    public final void write(JsonObject json) {

    }

    @Override
    public final boolean hasData() {
        return false;
    }
}
