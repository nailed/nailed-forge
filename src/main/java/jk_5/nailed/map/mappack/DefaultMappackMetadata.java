package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;

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
    public int difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public float spawnYaw;
    public float spawnPitch;
    public boolean pvpEnabled;
    public EnumGameType gamemode;
}
