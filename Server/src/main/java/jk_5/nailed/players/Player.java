package jk_5.nailed.players;

import com.mojang.authlib.GameProfile;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.TeleportHelper;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.ChatColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class Player {

    @Getter private final GameProfile gameProfile;
    @Setter private Map currentMap;
    @Getter private boolean online = false;
    @Getter @Setter private int teamSpeakClientID = -1;

    public void sendNotification(String message){
        this.sendNotification(message, null);
    }

    public void sendNotification(String message, ResourceLocation icon){
        this.sendNotification(message, icon, 0xFFFFFF);
    }

    public void sendNotification(String message, ResourceLocation icon, int iconColor){
        EntityPlayerMP entity = this.getEntity();
        if(entity != null){
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.Notification(message, icon, iconColor), entity);
        }
    }

    public void sendChat(String message){
        this.sendChat(new ChatComponentText(message));
    }

    public void sendChat(IChatComponent message){
        EntityPlayerMP entity = this.getEntity();
        if(entity != null) this.getEntity().func_145747_a(message); //sendChatToPlayer
    }

    public void sendPacket(Packet packet){
        this.getEntity().playerNetServerHandler.func_147359_a(packet); //sendPacketToPlayer
    }

    public EntityPlayerMP getEntity(){
        return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.getUsername());
    }

    public boolean isOp(){
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(this.getUsername());
    }

    public String getChatPrefix(){
        return this.getTeam().getColor() + (this.isOp() ? "@" : "") + this.getUsername() + ChatColor.RESET;
    }

    public Team getTeam(){
        return this.getCurrentMap().getTeamManager().getPlayerTeam(this);
    }

    public void setTeam(Team team){
        this.getCurrentMap().getTeamManager().setPlayerTeam(this, team);
    }

    public String getUsername(){
        return this.gameProfile.getName();
    }

    public String getId(){
        return this.gameProfile.getId();
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
        this.getEntity().setSpawnChunk(null, false);
    }

    public void teleportToMap(Map map){
        TeleportHelper.travelEntity(this.getEntity(), map.getSpawnTeleport());
    }
}
