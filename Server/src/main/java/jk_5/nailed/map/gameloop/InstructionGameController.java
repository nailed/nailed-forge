package jk_5.nailed.map.gameloop;

import com.google.common.collect.Maps;
import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.players.Team;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

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
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.TimeUpdate(true, data), this.controller.getMap().getID());
    }

    @Override
    public void broadcastNotification(String data, ResourceLocation icon){
        this.broadcastNotification(data, null, 0xFFFFFF);
    }

    @Override
    public void broadcastNotification(String data, ResourceLocation icon, int iconColor){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.Notification(data, icon, iconColor), this.controller.getMap().getID());
    }

    @Override
    public void broadcastSound(String sound) {
    }

    @Override
    public void broadcastNotification(String data) {
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(new NailedPacket.Notification(data, null, 0xFFFFFF), this.controller.getMap().getID());
    }

    @Override
    public void broadcastChatMessage(IChatComponent message) {
        MinecraftServer.getServer().getConfigurationManager().func_148537_a(new S02PacketChat(message), this.controller.getMap().getID());
    }

    @Override
    public jk_5.nailed.map.Map getMap() {
        return this.controller.getMap();
    }
}
