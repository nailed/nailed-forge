package jk_5.nailed.players;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.network.NailedSPH;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

    @Getter private final String username;
    @Setter private Map currentMap;
    @Getter private boolean online = false;

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

    public boolean isOp(){
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(this.username);
    }

    public String getChatPrefix(){
        return this.getTeam().getColor() + (this.isOp() ? "@" : "") + this.username + ChatColor.RESET;
    }

    public Team getTeam(){
        return this.getCurrentMap().getTeamManager().getPlayerTeam(this);
    }

    public void setTeam(Team team){
        this.getCurrentMap().getTeamManager().setPlayerTeam(this, team);
    }

    public Map getCurrentMap(){
        if(this.currentMap == null) this.currentMap = MapLoader.instance().getMap(0);
        return this.currentMap;
    }

    public void onLogin() {
        this.online = true;
    }

    public void onLogout() {
        this.online = false;
    }

    public void onChangedDimension() {

    }

    public void onRespawn() {
        if(this.getTeam() instanceof TeamUndefined) return;
        if(this.getTeam().shouldOverrideDefaultSpawnpoint()){
            this.getEntity().setSpawnChunk(null, false);
        }
    }
}
