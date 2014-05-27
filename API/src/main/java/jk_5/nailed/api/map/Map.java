package jk_5.nailed.api.map;

import java.io.*;
import java.util.*;
import javax.annotation.*;

import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import cpw.mods.fml.common.gameevent.*;

import jk_5.nailed.api.lua.*;
import jk_5.nailed.api.map.scoreboard.*;
import jk_5.nailed.api.map.sign.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.zone.*;
import jk_5.nailed.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Map {

    int getID();
    @Nullable
    Mappack getMappack();
    WorldServer getWorld();
    void setWorld(WorldServer world);
    boolean isLoaded();
    TeamManager getTeamManager();
    StatManager getStatManager();
    WeatherController getWeatherController();
    SignCommandHandler getSignCommandHandler();
    List<Player> getPlayers();
    int getAmountOfPlayers();
    String getSaveFileName();
    File getSaveFolder();
    TeleportOptions getSpawnTeleport();
    void broadcastChatMessage(String message);
    void broadcastChatMessage(IChatComponent component);
    Location getRandomSpawnpoint();
    void unloadAndRemove();
    void onPlayerJoined(Player player);
    void onPlayerLeft(Player player);
    void initMapServer();
    void onGameStarted();
    void onGameEnded();
    void onTick(TickEvent.ServerTickEvent event);
    GameManager getGameManager();
    ScoreboardManager getScoreboardManager();
    void broadcastPacket(Packet packet);
    int getMaxFoodLevel();
    int getMinFoodLevel();
    ZoneManager getZoneManager();
    void queueEvent(String event, Object... args);
    ChatComponentText getInfoBar();
    float getInfoBarProgress();
    LocationHandler getLocationHandler();
    MapLuaVm getLuaVm();
}
