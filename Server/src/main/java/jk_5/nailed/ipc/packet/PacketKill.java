package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;
import jk_5.nailed.api.player.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketKill extends IpcPacket {

    private Player killer;
    private Player victim;

    @Override
    public void read(JsonObject json) {

    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("username", this.killer.getUsername());
        json.addProperty("victim", this.killer.getUsername());
    }

    @Override
    public void processPacket() {

    }
}
