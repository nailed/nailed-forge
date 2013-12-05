package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import jk_5.nailed.players.TeamBuilder;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.config.ConfigFile;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class FileMappackMetadata implements MappackMetadata {

    private final ConfigFile config;
    @Getter public String name;
    @Getter public ChunkCoordinates spawnPoint;
    @Getter private List<TeamBuilder> defaultTeams;

    public FileMappackMetadata(ConfigFile config){
        this.config = config;
        int spawnX = config.getTag("spawnpoint").getTag("x").getIntValue(0);
        int spawnY = config.getTag("spawnpoint").getTag("y").getIntValue(64);
        int spawnZ = config.getTag("spawnpoint").getTag("z").getIntValue(0);
        this.name = config.getTag("map").getTag("name").getValue("");
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
    }
}
