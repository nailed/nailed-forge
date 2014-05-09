package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jk_5.nailed.api.WeatherType;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.PostGameAction;
import jk_5.nailed.api.map.SpawnRules;
import jk_5.nailed.api.map.team.TeamBuilder;
import jk_5.nailed.api.zone.NailedZone;
import jk_5.nailed.map.Location;
import jk_5.nailed.permissions.zone.NailedSecureZone;
import jk_5.nailed.util.ChatColor;
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
public class JsonMappackMetadata implements MappackMetadata {

    public String name;
    public Location spawnPoint;
    private List<TeamBuilder> defaultTeams;
    public Map<String, String> gameruleConfig;
    public EnumDifficulty difficulty;
    public String gameType;
    public boolean preventingBlockBreak;
    public boolean pvpEnabled;
    public boolean fallDamageDisabled;
    public WorldSettings.GameType gamemode;
    public boolean choosingRandomSpawnpointAtRespawn;
    public List<Location> randomSpawnpoints;
    public String startWhen = "false";
    public EnumSet<WeatherType> permittedWeatherTypes;
    public SpawnRules spawnRules;
    public int minFoodLevel;
    public int maxFoodLevel;
    public int minHealth;
    public int maxHealth;
    public PostGameAction postGameAction;
    public List<NailedZone> zones;

    public JsonMappackMetadata(JsonObject json){
        this.spawnPoint = json.has("spawnpoint") ? Location.readFrom(json.get("spawnpoint").getAsJsonObject()) : new Location(0, 64, 0, 0, 0);
        this.name = json.has("name") ? json.get("name").getAsString() : null;
        this.pvpEnabled = !json.has("pvpEnabled") || json.get("pvpEnabled").getAsBoolean();
        this.gamemode = WorldSettings.GameType.getByID(json.has("defaultGamemode") ? json.get("defaultGamemode").getAsInt() : 2);
        this.difficulty = EnumDifficulty.getDifficultyEnum(json.has("difficulty") ? json.get("difficulty").getAsInt() : 2);
        this.gameType = json.has("gametype") ? json.get("gametype").getAsString() : "default";
        this.preventingBlockBreak = json.has("preventBlockBreak") && json.get("preventBlockBreak").getAsBoolean();
        this.fallDamageDisabled = json.has("fallDamageDisabled") && json.get("FallDamageDisabled").getAsBoolean();
        this.choosingRandomSpawnpointAtRespawn = json.has("randomSpawnpointOnRespawn") && json.get("randomSpawnpointOnRespawn").getAsBoolean();
        this.startWhen = json.has("startGameWhen") ? json.get("startGameWhen").getAsString() : "false";
        this.postGameAction = json.has("postGameAction") ? PostGameAction.fromType(json.get("postGameAction").getAsString()) : PostGameAction.NOTHING;

        if(json.has("spawns")){
            this.spawnRules = new Gson().fromJson(json.get("spawns"), SpawnRules.class);
            this.spawnRules.refresh();
        }else{
            this.spawnRules = new SpawnRules();
        }

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

        this.zones = Lists.newArrayList();
        if(json.has("zones")){
            JsonArray zones = json.getAsJsonArray("zones");
            for(JsonElement z : zones){
                NailedZone zone = NailedSecureZone.readFrom(z.getAsJsonObject());
                if( zone != null) this.zones.add(zone);
            }
        }

        this.minFoodLevel = 0;
        this.maxFoodLevel = -1;
        this.minHealth = 5;
        this.maxHealth = 20;
        if(json.has("minFoodLevel")){
            this.minFoodLevel = json.get("minFoodLevel").getAsInt();
        }
        if(json.has("maxFoodLevel")){
            this.maxFoodLevel = json.get("maxFoodLevel").getAsInt();
        }
        if(json.has("minHealth")){
            this.minHealth = json.get("minHealth").getAsInt();
        }
        if(json.has("maxHealth")){
            this.maxHealth = json.get("maxHealth").getAsInt();
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
    public boolean isFallDamageDisabled() { return this.fallDamageDisabled; }

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

    @Override
    public int getMinFoodLevel(){
        return this.minFoodLevel;
    }

    @Override
    public int getMaxFoodLevel(){
        return this.maxFoodLevel;
    }

    @Override
    public int getMinHealth(){ return this.minHealth; }

    @Override
    public int getMaxHealth(){ return this.maxHealth; }

    @Override
    public PostGameAction getPostGameAction() { return this.postGameAction; }

    @Override
    public List<NailedZone> getMapZones(){
        return this.zones;
    }
}
