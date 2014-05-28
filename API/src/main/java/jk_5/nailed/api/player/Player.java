package jk_5.nailed.api.player;

import java.util.*;

import com.mojang.authlib.*;

import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.camera.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.*;

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
