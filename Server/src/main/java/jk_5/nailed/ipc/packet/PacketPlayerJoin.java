package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;
import jk_5.nailed.players.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayerJoin extends IpcPacket {

    private Player player;

    @Override
    public void read(JsonObject json) {

    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("username", this.player.getUsername());
    }

    @Override
    public void processPacket() {

    }
}
