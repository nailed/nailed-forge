package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import jk_5.nailed.util.WeatherType;
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
    Spawnpoint getSpawnPoint();
    List<TeamBuilder> getDefaultTeams();
    boolean isSpawnHostileMobs();
    boolean isSpawnFriendlyMobs();
    Map<String, String> getGameruleConfig();
    EnumDifficulty getDifficulty();
    String getGameType();
    boolean isPreventingBlockBreak();
    boolean isPvpEnabled();
    WorldSettings.GameType getGamemode();
    boolean isChoosingRandomSpawnpointAtRespawn();
    List<Spawnpoint> getRandomSpawnpoints();
    String getStartWhen();
    EnumSet<WeatherType> getPermittedWeatherTypes();
}
