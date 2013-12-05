package jk_5.nailed.map.instruction;

import jk_5.nailed.players.Team;
import net.minecraft.util.ChatMessageComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public interface GameController {

    void setWinner(Team team);

    void save(String key, Object value);
    Object load(String key);

    void broadcastTimeRemaining(String data);
    void broadcastSound(String sound);
    void broadcastNotification(String data);
    void broadcastChatMessage(ChatMessageComponent message);
}
