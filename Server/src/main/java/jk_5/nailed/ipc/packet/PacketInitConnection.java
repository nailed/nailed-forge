package jk_5.nailed.ipc.packet;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.base64.Base64;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.player.Player;

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
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.isOnline()){
                onlinePlayers.add(new JsonPrimitive(player.getUsername()));
            }
        }
        json.add("players", onlinePlayers);
        JsonArray mappacks = new JsonArray();
        for(Mappack mappack : NailedAPI.getMappackLoader().getMappacks()){
            JsonObject obj = new JsonObject();
            ByteBuf icon = Base64.encode(mappack.getMappackIcon());
            obj.addProperty("id", mappack.getMappackID());
            obj.addProperty("name", mappack.getMappackMetadata().getName());
            obj.addProperty("icon", "data:image/png;base64," + icon.toString(Charsets.UTF_8));
            obj.addProperty("isLobby", mappack.getMappackID().equals("lobby"));
            obj.addProperty("hidden", false);
            mappacks.add(obj);
        }
        json.add("mappacks", mappacks);
    }

    @Override
    public void processPacket() {

    }
}
