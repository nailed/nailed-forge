package jk_5.nailed.api.map;

import java.util.*;
import java.util.Map;

import net.minecraft.world.*;

import jk_5.nailed.api.map.team.*;
import jk_5.nailed.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackMetadata {

    String getName();
    Location getSpawnPoint();
    List<TeamBuilder> getDefaultTeams();
    Map<String, String> getGameruleConfig();
    EnumDifficulty getDifficulty();
    String getGameType();
    boolean isPreventingBlockBreak();
    boolean isPvpEnabled();
    boolean isFallDamageDisabled();
    WorldSettings.GameType getGamemode();
    boolean isChoosingRandomSpawnpointAtRespawn();
    List<Location> getRandomSpawnpoints();
    String getStartWhen();
    SpawnRules getSpawnRules();
    int getMaxFoodLevel();
    int getMinFoodLevel();
    int getMaxHealth();
    int getMinHealth();
    PostGameAction getPostGameAction();
    HashMap<String, Location> getLocations();
}
