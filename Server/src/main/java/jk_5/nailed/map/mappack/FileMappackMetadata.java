package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jk_5.nailed.api.WeatherType;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.SpawnRules;
import jk_5.nailed.api.map.team.TeamBuilder;
import jk_5.nailed.map.Location;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.config.ConfigFile;
import jk_5.nailed.util.config.ConfigTag;
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
public class FileMappackMetadata implements MappackMetadata {

    private final ConfigFile config;
    public String name;
    public Location spawnPoint;
    private List<TeamBuilder> defaultTeams;
    public Map<String, String> gameruleConfig;
    public EnumDifficulty difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public boolean pvpEnabled;
    public WorldSettings.GameType gamemode;
    public boolean choosingRandomSpawnpointAtRespawn;
    public List<Location> randomSpawnpoints;
    public String startWhen;
    public EnumSet<WeatherType> permittedWeatherTypes;
    public SpawnRules spawnRules = new SpawnRules(); //TODO

    public FileMappackMetadata(ConfigFile config){
        this.config = config;
        this.spawnPoint = Location.readFrom(config.getTag("spawnpoint"));
        this.name = config.getTag("map").getTag("name").getValue("");
        this.pvpEnabled = config.getTag("map").getTag("pvp").getBooleanValue(true);
        this.gamemode = WorldSettings.GameType.getByID(config.getTag("map").getTag("gamemode").getIntValue(2));
        this.difficulty = EnumDifficulty.getDifficultyEnum(config.getTag("map").getTag("difficulty").getIntValue(2));
        this.gameType = config.getTag("map").getTag("gametype").getValue("default");
        this.preventingBlockBreak = config.getTag("map").getTag("preventBlockBreak").getBooleanValue(false);
        this.choosingRandomSpawnpointAtRespawn = config.getTag("map").getTag("randomSpawnpointOnRespawn").getBooleanValue(false);
        this.startWhen = config.getTag("map").getTag("startGameWhen").getValue("false");

        this.defaultTeams = Lists.newArrayList();
        ConfigTag tags = this.config.getTag("teams");
        for(ConfigTag tag : tags.getSortedTagList()){
            TeamBuilder team = new jk_5.nailed.map.team.TeamBuilder();
            team.setInternalName(tag.name);
            team.setName(tag.getTag("name").getValue(tag.name));
            team.setColor(ChatColor.getByName(tag.getTag("color").getValue(ChatColor.WHITE.name())));
            team.setFriendlyFire(tag.getTag("friendlyfire").getBooleanValue(false));
            team.setSeeFriendlyInvisibles(tag.getTag("friendlyinvisibles").getBooleanValue(true));
            this.defaultTeams.add(team);
        }

        this.gameruleConfig = Maps.newHashMap();
        ConfigTag gameruleTag = this.config.getTag("gamerule");
        for(ConfigTag tag : gameruleTag.getSortedTagList()){
            this.gameruleConfig.put(tag.name, tag.getValue());
        }

        this.randomSpawnpoints = Lists.newArrayList();
        ConfigTag spawnpointsTag = this.config.getTag("randomSpawnpoints");
        for(ConfigTag tag : spawnpointsTag.getSortedTagList()){
            this.randomSpawnpoints.add(Location.readFrom(tag));
        }

        this.permittedWeatherTypes = EnumSet.noneOf(WeatherType.class);
        ConfigTag weatherTag = this.config.getTag("permittedWeather");
        for(ConfigTag tag : weatherTag.getSortedTagList()){
            WeatherType type = WeatherType.valueOf(tag.name.toUpperCase());
            if(type == null) continue;
            if(tag.getBooleanValue(true)){
                this.permittedWeatherTypes.add(type);
            }
        }
    }

    public ConfigFile getConfig() {
        return this.config;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Location getSpawnPoint() {
        return this.spawnPoint;
    }

    @Override
    public List<TeamBuilder> getDefaultTeams() {
        return this.defaultTeams;
    }

    @Override
    public Map<String, String> getGameruleConfig() {
        return this.gameruleConfig;
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public String getGameType() {
        return this.gameType;
    }

    @Override
    public boolean isPreventingBlockBreak() {
        return this.preventingBlockBreak;
    }

    @Override
    public boolean isPvpEnabled() {
        return this.pvpEnabled;
    }

    @Override
    public WorldSettings.GameType getGamemode() {
        return this.gamemode;
    }

    @Override
    public boolean isChoosingRandomSpawnpointAtRespawn() {
        return this.choosingRandomSpawnpointAtRespawn;
    }

    @Override
    public List<Location> getRandomSpawnpoints() {
        return this.randomSpawnpoints;
    }

    @Override
    public String getStartWhen() {
        return this.startWhen;
    }

    @Override
    public EnumSet<WeatherType> getPermittedWeatherTypes() {
        return this.permittedWeatherTypes;
    }

    @Override
    public SpawnRules getSpawnRules() {
        return this.spawnRules;
    }
}
