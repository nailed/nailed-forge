package jk_5.nailed.map.mappack;

import jk_5.nailed.api.WeatherType;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.Spawnpoint;
import jk_5.nailed.api.map.team.TeamBuilder;
import lombok.Getter;
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
@Getter
public class DefaultMappackMetadata implements MappackMetadata {

    public String name;
    public Spawnpoint spawnPoint;
    public List<TeamBuilder> defaultTeams;
    public boolean spawnFriendlyMobs;
    public boolean spawnHostileMobs;
    public Map<String, String> gameruleConfig;
    public EnumDifficulty difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public boolean pvpEnabled;
    public WorldSettings.GameType gamemode;
    public boolean choosingRandomSpawnpointAtRespawn;
    public List<Spawnpoint> randomSpawnpoints;
    public String startWhen;
    public EnumSet<WeatherType> permittedWeatherTypes;
}
