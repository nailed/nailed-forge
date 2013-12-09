package jk_5.nailed.ipc.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketInitConnection extends IpcPacket {

    @Override
    public void read(JsonObject json) {

    }

    @Override
    public void write(JsonObject json) {
        JsonArray onlinePlayers = new JsonArray();
        for(Player player : PlayerRegistry.instance().getPlayers()){
            if(player.isOnline()){
                onlinePlayers.add(new JsonPrimitive(player.getUsername()));
            }
        }
        json.add("players", onlinePlayers);
        JsonArray mappacks = new JsonArray();
        for(Mappack mappack : MapLoader.instance().getMappacks()){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", mappack.getMappackID());
            obj.addProperty("name", mappack.getMappackMetadata().getName());
            mappacks.add(obj);
        }
        json.add("mappacks", mappacks);
    }

    @Override
    public void processPacket() {

    }
}
