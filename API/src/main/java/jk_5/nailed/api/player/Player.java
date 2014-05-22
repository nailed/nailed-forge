package jk_5.nailed.api.player;

import com.mojang.authlib.GameProfile;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.scripting.ILuaObject;
import jk_5.nailed.map.Location;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Player extends PossibleWinner, ILuaObject {

    public GameProfile getGameProfile();
    public Map getCurrentMap();
    public void setCurrentMap(Map map);
    public boolean isOnline();
    public int getFps();
    public void setFps(int fps);
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
    public void setLocation(Location location);
    public Gamemode getGameMode();
    public void setGameMode(Gamemode mode);
    public boolean hasPermission(String node);
    public void setSpawnpoint(Location spawnpoint);
    public Location getSpawnpoint();
    public void setPdaID(int id);
    public int getPdaID();
    public NetHandlerPlayServer getNetHandler();
    public void sendTimeUpdate(String message);
    public boolean isEditModeEnabled();
    public void setEditModeEnabled(boolean editModeEnabled) throws IncompatibleClientException;
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
    public PlayerClient getClient();
    public void setClient(PlayerClient client);
    public void setMoving(IMovement movement);
    public void kick(String reason);
}
