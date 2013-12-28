package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jk_5.nailed.players.TeamBuilder;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.config.ConfigFile;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@Getter
public class FileMappackMetadata implements MappackMetadata {

    private final ConfigFile config;
    public String name;
    public ChunkCoordinates spawnPoint;
    private List<TeamBuilder> defaultTeams;
    public boolean spawnFriendlyMobs;
    public boolean spawnHostileMobs;
    public Map<String, String> gameruleConfig;
    public int difficulty;
    public String gameType;
    public boolean preventingBlockBreak;

    public FileMappackMetadata(ConfigFile config){
        this.config = config;
        int spawnX = config.getTag("spawnpoint").getTag("x").getIntValue(0);
        int spawnY = config.getTag("spawnpoint").getTag("y").getIntValue(64);
        int spawnZ = config.getTag("spawnpoint").getTag("z").getIntValue(0);
        this.name = config.getTag("map").getTag("name").getValue("");
        this.spawnHostileMobs = config.getTag("map").getTag("spawn-hostile-mobs").getBooleanValue(true);
        this.spawnFriendlyMobs = config.getTag("map").getTag("spawn-friendly-mobs").getBooleanValue(true);
        this.name = config.getTag("map").getTag("name").getValue("");
        this.difficulty = config.getTag("map").getTag("difficulty").getIntValue(2);
        this.gameType = config.getTag("map").getTag("gametype").getValue("default");
        this.preventingBlockBreak = config.getTag("map").getTag("preventBlockBreak").getBooleanValue(false);
        this.spawnPoint = new ChunkCoordinates(spawnX, spawnY, spawnZ);

        this.defaultTeams = Lists.newArrayList();
        ConfigTag tags = this.config.getTag("teams");
        for(ConfigTag tag : tags.getSortedTagList()){
            TeamBuilder team = new TeamBuilder();
            team.setInternalName(tag.name);
            team.setName(tag.getTag("name").getValue(tag.name));
            team.setColor(ChatColor.getByName(tag.getTag("color").getValue(ChatColor.WHITE.name())));
            team.setFriendlyFire(tag.getTag("friendlyfire").getBooleanValue(false));
            team.setSeeFriendlyInvisibles(tag.getTag("friendlyinvisibles").getBooleanValue(true));
            this.defaultTeams.add(team);
        }

        this.gameruleConfig = Maps.newHashMap();
        ConfigTag gameruleTag = this.config.getTag("gamerule", false);
        if(gameruleTag != null){
            for(ConfigTag tag : gameruleTag.getSortedTagList()){
                this.gameruleConfig.put(tag.name, tag.getValue());
            }
        }
    }
}
