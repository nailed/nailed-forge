package jk_5.nailed.api.map;

import jk_5.nailed.api.map.sign.SignCommandHandler;
import jk_5.nailed.api.map.stat.StatManager;
import jk_5.nailed.api.map.team.TeamManager;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Map {

    public int getID();
    public Mappack getMappack();
    public World getWorld();
    public void setWorld(World world);
    public boolean isLoaded();
    public TeamManager getTeamManager();
    public StatManager getStatManager();
    public InstructionController getInstructionController();
    public WeatherController getWeatherController();
    public SignCommandHandler getSignCommandHandler();
    public List<Player> getPlayers();
    public int getAmountOfPlayers();
    public String getSaveFileName();
    public File getSaveFolder();
    public TeleportOptions getSpawnTeleport();
    public void broadcastNotification(String message);
    public void broadcastChatMessage(String message);
    public void broadcastChatMessage(IChatComponent component);
    public Spawnpoint getRandomSpawnpoint();
    public void unloadAndRemove();
    public void reloadFromMappack();
    public void onPlayerJoined(Player player);
    public void onPlayerLeft(Player player);
    public void initMapServer();
    public void onGameStarted();
    public void onGameEnded();
}
