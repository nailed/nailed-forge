package jk_5.nailed.players;

import jk_5.nailed.network.NailedSPH;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class Player {

    @Getter
    private final String username;

    public void setTimeLeft(int seconds){
        NailedSPH.sendTimeUpdate(this.getEntity(), "Time left: " + ChatColor.GREEN + Utils.secondsToShortTimeString(seconds));
    }

    public void sendNotification(String message){
        NailedSPH.sendNotification(this.getEntity(), message);
    }

    public void sendChat(String message){
        this.sendChat(ChatMessageComponent.createFromText(message));
    }

    public void sendChat(ChatMessageComponent message){
        this.getEntity().sendChatToPlayer(message);
    }

    public EntityPlayerMP getEntity(){
        return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.username);
    }
}
