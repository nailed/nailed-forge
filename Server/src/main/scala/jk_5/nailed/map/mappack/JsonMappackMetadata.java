package jk_5.nailed.map.mappack;

import java.util.*;
import java.util.Map;

import com.google.common.collect.*;
import com.google.gson.*;

import net.minecraft.world.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.map.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class JsonMappackMetadata implements MappackMetadata {

    public String name;
    public Location spawnPoint;
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
    public SpawnRules spawnRules;
    public int minFoodLevel;
    public int maxFoodLevel;
    public int minHealth;
    public int maxHealth;
    public PostGameAction postGameAction;
    public HashMap<String, Location> locations;
    private List<TeamBuilder> defaultTeams;

    public JsonMappackMetadata(JsonObject json) {
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
        this.minFoodLevel = json.has("minFoodLevel") ? json.get("minFoodLevel").getAsInt() : 0;
        this.maxFoodLevel = json.has("maxFoodLevel") ? json.get("maxFoodLevel").getAsInt() : -1;
        this.minHealth = json.has("minHealth") ? json.get("minHealth").getAsInt() : 0;
        this.maxHealth = json.has("maxHealth") ? json.get("maxHealth").getAsInt() : 20;

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

        this.locations = Maps.newHashMap();
        if(json.has("locations")){
            JsonArray array = json.getAsJsonArray("locations");
            for(JsonElement element : array){
                if(element instanceof JsonObject){
                    this.locations.put(((JsonObject) element).get("name").getAsString(), Location.readFrom((JsonObject) element));
                }
            }

        }
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
    public boolean isFallDamageDisabled() {
        return this.fallDamageDisabled;
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
    public SpawnRules getSpawnRules() {
        return this.spawnRules;
    }

    @Override
    public int getMinFoodLevel() {
        return this.minFoodLevel;
    }

    @Override
    public int getMaxFoodLevel() {
        return this.maxFoodLevel;
    }

    @Override
    public int getMinHealth() {
        return this.minHealth;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public PostGameAction getPostGameAction() {
        return this.postGameAction;
    }

    @Override
    public HashMap<String, Location> getLocations() {
        return this.locations;
    }
}
