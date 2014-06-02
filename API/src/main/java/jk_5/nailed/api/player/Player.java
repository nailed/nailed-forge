package jk_5.nailed.api.player;

import java.util.List;
import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;

import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.scripting.ILuaObject;
import jk_5.nailed.map.Location;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Player extends PossibleWinner, ILuaObject {

    GameProfile getGameProfile();
    Map getCurrentMap();
    void setCurrentMap(Map map);
    boolean isOnline();
    int getFps();
    void setFps(int fps);
    void sendChat(String message);
    void sendChat(IChatComponent message);
    void sendPacket(Packet packet);
    EntityPlayerMP getEntity();
    boolean isOp();
    String getChatPrefix();
    Team getTeam();
    void setTeam(Team team);
    String getUsername();
    String getId();
    void onLogin();
    void onLogout();
    void onChangedDimension();
    void onRespawn();
    void teleportToMap(Map map);
    Location getLocation();
    void setLocation(Location location);
    Gamemode getGameMode();
    void setGameMode(Gamemode mode);
    boolean hasPermission(String node);
    void setSpawnpoint(Location spawnpoint);
    Location getSpawnpoint();
    NetHandlerPlayServer getNetHandler();
    void sendTimeUpdate(String message);
    boolean isEditModeEnabled();
    void setEditModeEnabled(boolean editModeEnabled) throws IncompatibleClientException;
    NailedWebUser getWebUser();
    void setWebUser(NailedWebUser webUser);
    void teleportToLobby();
    void setMinHealth(int min);
    int getMinHealth();
    void setMaxHealth(int max);
    int getMaxHealth();
    List<Player> getPlayersVisible();
    void removePlayerVisible(Player player);
    void addPlayerVisible(Player player);
    int getNumPlayersVisible();
    void setPlayersVisible(List<Player> list);
    void replacePlayerVisible(Player player, List<Player> players, Random random);
    PlayerClient getClient();
    void setClient(PlayerClient client);
    void setMoving(IMovement movement);
    void kick(String reason);
}
