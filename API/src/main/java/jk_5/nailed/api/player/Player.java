package jk_5.nailed.api.player;

import com.mojang.authlib.GameProfile;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.Spawnpoint;
import jk_5.nailed.api.map.team.Team;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Player extends PossibleWinner {

    public GameProfile getGameProfile();
    public Map getCurrentMap();
    public void setCurrentMap(Map map);
    public boolean isOnline();
    public int getFps();
    public void setFps(int fps);
    public void sendNotification(String message);
    public void sendNotification(String message, ResourceLocation icon);
    public void sendNotification(String message, ResourceLocation icon, int iconColor);
    public void sendChat(String message);
    public void sendChat(IChatComponent message);
    public void sendPacket(Packet packet);
    public EntityPlayerMP getEntity();
    public boolean isOp();
    public String getChatPrefix();
    public Team getTeam();
    public void setTeam(Team team);
    public String getUsername();
    public String getId();
    public void onLogin();
    public void onLogout();
    public void onChangedDimension();
    public void onRespawn();
    public void teleportToMap(Map map);
    public Spawnpoint getLocation();
    public Gamemode getGameMode();
    public void setGameMode(Gamemode mode);
    public boolean hasPermission(String node);
    public void setSpawnpoint(Spawnpoint spawnpoint);
    public Spawnpoint getSpawnpoint();
    public void setPdaID(int id);
    public int getPdaID();
    public NetHandlerPlayServer getNetHandler();
    public void sendTimeUpdate(String argument);
}
