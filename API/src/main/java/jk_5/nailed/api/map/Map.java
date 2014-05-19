package jk_5.nailed.api.map;

import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.api.map.scoreboard.ScoreboardManager;
import jk_5.nailed.api.map.sign.SignCommandHandler;
import jk_5.nailed.api.map.stat.StatManager;
import jk_5.nailed.api.map.team.TeamManager;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.zone.ZoneManager;
import jk_5.nailed.map.Location;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Map {

    public int getID();
    @Nullable public Mappack getMappack();
    public WorldServer getWorld();
    public void setWorld(WorldServer world);
    public boolean isLoaded();
    public TeamManager getTeamManager();
    public StatManager getStatManager();
    public WeatherController getWeatherController();
    public SignCommandHandler getSignCommandHandler();
    public List<Player> getPlayers();
    public int getAmountOfPlayers();
    public String getSaveFileName();
    public File getSaveFolder();
    public TeleportOptions getSpawnTeleport();
    public void broadcastChatMessage(String message);
    public void broadcastChatMessage(IChatComponent component);
    public Location getRandomSpawnpoint();
    public void unloadAndRemove();
    public void onPlayerJoined(Player player);
    public void onPlayerLeft(Player player);
    public void initMapServer();
    public void onGameStarted();
    public void onGameEnded();
    public void onTick(TickEvent.ServerTickEvent event);
    public GameManager getGameManager();
    public ScoreboardManager getScoreboardManager();
    public void broadcastPacket(Packet packet);
    public int getMaxFoodLevel();
    public int getMinFoodLevel();
    public ZoneManager getZoneManager();
    public void queueEvent(String event, Object... args);
}
