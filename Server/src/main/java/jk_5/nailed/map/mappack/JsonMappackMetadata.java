package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jk_5.nailed.api.WeatherType;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.SpawnRules;
import jk_5.nailed.api.map.team.TeamBuilder;
import jk_5.nailed.map.Location;
import jk_5.nailed.util.ChatColor;
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
public class JsonMappackMetadata implements MappackMetadata {

    private final JsonObject json;
    public String name;
    public Location spawnPoint;
    private List<TeamBuilder> defaultTeams;
    public boolean spawnFriendlyMobs = false;
    public boolean spawnHostileMobs = false;
    public Map<String, String> gameruleConfig;
    public EnumDifficulty difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public boolean pvpEnabled;
    public WorldSettings.GameType gamemode;
    public boolean choosingRandomSpawnpointAtRespawn;
    public List<Location> randomSpawnpoints;
    public String startWhen = "false";
    public EnumSet<WeatherType> permittedWeatherTypes;
    public SpawnRules spawnRules;

    public JsonMappackMetadata(JsonObject json){
        this.json = json;
        this.spawnPoint = json.has("spawnpoint") ? Location.readFrom(json.get("spawnpoint").getAsJsonObject()) : new Location(0, 64, 0, 0, 0);
        this.name = json.get("name").getAsString();
        this.pvpEnabled = !json.has("pvpEnabled") || json.get("pvpEnabled").getAsBoolean();
        this.gamemode = WorldSettings.GameType.getByID(json.has("defaultGamemode") ? json.get("defaultGamemode").getAsInt() : 2);
        //this.spawnHostileMobs = config.getTag("map").getTag("spawn-hostile-mobs").getBooleanValue(true);
        //this.spawnFriendlyMobs = config.getTag("map").getTag("spawn-friendly-mobs").getBooleanValue(true);
        this.difficulty = EnumDifficulty.getDifficultyEnum(json.has("difficulty") ? json.get("difficulty").getAsInt() : 2);
        this.gameType = json.has("gametype") ? json.get("gametype").getAsString() : "default";
        this.preventingBlockBreak = json.has("preventBlockBreak") && json.get("preventBlockBreak").getAsBoolean();
        this.choosingRandomSpawnpointAtRespawn = json.has("randomSpawnpointOnRespawn") && json.get("randomSpawnpointOnRespawn").getAsBoolean();
        //this.startWhen = config.getTag("map").getTag("startGameWhen").getValue("false");
        this.spawnRules = new Gson().fromJson(json.get("spawns"), SpawnRules.class);
        this.spawnRules.refresh();

        this.defaultTeams = Lists.newArrayList();
        if(json.has("teams")){
            JsonArray tags = json.getAsJsonArray("teams");
            for(JsonElement t : tags){
                JsonObject tag = t.getAsJsonObject();
                TeamBuilder team = new jk_5.nailed.map.team.TeamBuilder();
                team.setInternalName(tag.get("id").getAsString());
                team.setName(tag.get(tag.has("name") ? "name" : "id").getAsString());
                team.setColor(tag.has("color") ? ChatColor.getByName(tag.get("color").getAsString()) : ChatColor.WHITE);
                team.setFriendlyFire(json.has("friendlyfire") && json.get("friendlyfire").getAsBoolean());
                team.setSeeFriendlyInvisibles(json.has("seefriendlyinvisibles") && json.get("seefriendlyinvisibles").getAsBoolean());
                this.defaultTeams.add(team);
            }
        }

        this.gameruleConfig = Maps.newHashMap();
        if(json.has("gamerules")){
            JsonObject gamerules = json.get("gamerules").getAsJsonObject();
            for(Map.Entry<String, JsonElement> e : gamerules.entrySet()){
                this.gameruleConfig.put(e.getKey(), e.getValue().getAsString());
            }
        }

        this.randomSpawnpoints = Lists.newArrayList();
        if(json.has("randomspawnpoints")){
            JsonArray points = json.getAsJsonArray("randomspawnpoints");
            for(JsonElement t : points){
                this.randomSpawnpoints.add(Location.readFrom(t.getAsJsonObject()));
            }
        }

        //TODO: this is not used yet
        /*this.permittedWeatherTypes = EnumSet.noneOf(WeatherType.class);
        ConfigTag weatherTag = this.config.getTag("permittedWeather");
        for(ConfigTag tag : weatherTag.getSortedTagList()){
            WeatherType type = WeatherType.valueOf(tag.name.toUpperCase());
            if(type == null) continue;
            if(tag.getBooleanValue(true)){
                this.permittedWeatherTypes.add(type);
            }
        }*/
    }
}
