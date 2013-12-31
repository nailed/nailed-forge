package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

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
    public ChunkCoordinates spawnPoint;
    public List<TeamBuilder> defaultTeams;
    public boolean spawnFriendlyMobs;
    public boolean spawnHostileMobs;
    public Map<String, String> gameruleConfig;
    public EnumDifficulty difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public float spawnYaw;
    public float spawnPitch;
    public boolean pvpEnabled;
    public WorldSettings.GameType gamemode;
    public boolean choosingRandomSpawnpointAtRespawn;
    public List<Spawnpoint> randomSpawnpoints;
    public String startWhen;
}
