package jk_5.nailed.api.map;

import jk_5.nailed.api.WeatherType;
import jk_5.nailed.api.map.team.TeamBuilder;
import jk_5.nailed.map.Location;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
    WorldSettings.GameType getGamemode();
    boolean isChoosingRandomSpawnpointAtRespawn();
    List<Location> getRandomSpawnpoints();
    String getStartWhen();
    @Deprecated EnumSet<WeatherType> getPermittedWeatherTypes();
    SpawnRules getSpawnRules();
    int getMaxFoodLevel();
    int getMinFoodLevel();
    int getMaxHealth();
    int getMinHealth();
    PostGameAction getPostGameAction();
}
