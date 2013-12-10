package jk_5.nailed.map.gameloop;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.network.Packets;
import jk_5.nailed.players.Team;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.util.ChatMessageComponent;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class InstructionGameController implements GameController {

    private final Map<String, Object> storage = Maps.newHashMap();
    private final InstructionController controller;

    @Override
    public void save(String key, Object value) {
        if(this.storage.containsKey(key)) this.storage.remove(key);
        this.storage.put(key, value);
    }

    @Override
    public Object load(String key) {
        return this.storage.get(key);
    }

    @Override
    public void setWinner(Team team) {
        this.controller.setWinner(team);
    }

    @Override
    public void broadcastTimeRemaining(String data) {
        Packets.TIME_UPDATE.newPacket().writeString(data).sendToDimension(this.controller.getMap().getID());
    }

    @Override
    public void broadcastSound(String sound) {
    }

    @Override
    public void broadcastNotification(String data) {
        Packets.NOTIFICATION.newPacket().writeByte(0).writeString(data).sendToDimension(this.controller.getMap().getID());
    }

    @Override
    public void broadcastChatMessage(ChatMessageComponent message) {
        PacketDispatcher.sendPacketToAllInDimension(new Packet3Chat(message), this.controller.getMap().getID());
    }

    @Override
    public jk_5.nailed.map.Map getMap() {
        return this.controller.getMap();
    }
}
