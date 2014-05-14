package jk_5.nailed.api.player;

import com.mojang.authlib.GameProfile;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.map.Location;
import jk_5.nailed.map.Spawnpoint;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Random;

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
    public Location getLocation();
    public Gamemode getGameMode();
    public void setGameMode(Gamemode mode);
    public boolean hasPermission(String node);
    public void setSpawnpoint(Spawnpoint spawnpoint);
    public Spawnpoint getSpawnpoint();
    public void setPdaID(int id);
    public int getPdaID();
    public NetHandlerPlayServer getNetHandler();
    public void sendTimeUpdate(String argument);
    public boolean isEditModeEnabled();
    public void setEditModeEnabled(boolean editModeEnabled);
    public NailedWebUser getWebUser();
    public void setWebUser(NailedWebUser webUser);
    public void teleportToLobby();
    public void setMinHealth(int min);
    public int getMinHealth();
    public void setMaxHealth(int max);
    public int getMaxHealth();
    public List<Player> getPlayersVisible();
    public void removePlayerVisible(Player player);
    public void addPlayerVisible(Player player);
    public int getNumPlayersVisible();
    public void setPlayersVisible(List<Player> list);
    public void replacePlayerVisible(Player player, List<Player> players, Random random);
    public boolean isNailed();
    public void setNailed(boolean isNailed);
}
