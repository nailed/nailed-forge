package jk_5.nailed.map.script.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.Spawnpoint;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.script.*;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class MapApi implements ILuaAPI {

    private final IAPIEnvironment env;
    private Map map;

    @Override
    public String[] getNames(){
        return new String[]{"map"};
    }

    @Override
    public void startup(){
        this.map = NailedAPI.getMapLoader().getMap(env.getMachine().getMachine().getWorld());
    }

    @Override
    public void advance(double paramDouble){

    }

    @Override
    public void shutdown(){

    }

    @Override
    public String[] getMethodNames(){
        return new String[]{
                "sendNotification",
                "sendChatComponent",
                "sendChat",
                "watchUnready",
                "winnerInterrupt",
                "getPlayers",
                "getTeams",
                "forEachPlayer",
                "forEachTeam",
                "getTeam",
                "setDifficulty",
                "sendTimeUpdate",
                "setTime"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0: //sendNotification
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.broadcastNotification((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 1: //sendChatComponent
                if(arguments.length == 1 && arguments[0] instanceof String){
                    try{
                        IChatComponent comp = IChatComponent.Serializer.func_150699_a((String) arguments[0]);
                        this.map.broadcastChatMessage(comp);
                    }catch(JsonParseException e){
                        throw new Exception("Chat message is not of json format");
                    }
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 2: //sendChat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.broadcastChatMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 3: //watchUnready
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWatchUnready((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 4: //winnerInterrupt
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWinnerInterrupt((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 5: //getPlayers
                List<Player> players = this.map.getPlayers();
                java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                for(int i = 0; i < players.size(); i++){
                    table.put(i + 1, this.wrapPlayer(players.get(i)));
                }
                return new Object[]{table};
            case 6: //getTeams
                List<Team> teams = this.map.getTeamManager().getTeams();
                java.util.Map<Integer, ILuaObject> table1 = Maps.newHashMap();
                for(int i = 0; i < teams.size(); i++){
                    table1.put(i + 1, this.wrapTeam(teams.get(i)));
                }
                return new Object[]{table1};
            case 7: //forEachPlayer
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Player> players1 = this.map.getPlayers();
                    for(int i = 0; i < players1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{this.wrapPlayer(players1.get(i))}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
            case 8: //forEachTeam
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Team> teams1 = this.map.getTeamManager().getTeams();
                    for(int i = 0; i < teams1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{this.wrapTeam(teams1.get(i))}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
            case 9: //getTeam
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    return new Object[]{this.wrapTeam(this.map.getTeamManager().getTeam((String) arguments[0]))};
                }else{
                    throw new Exception("Excpected 1 string argument");
                }
            case 10: //setDifficulty
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    MappackMetadata meta = this.map.getMappack().getMappackMetadata();
                    World server = this.map.getWorld();
                    int difficulty = ((Double) arguments[0]).intValue();
                    server.difficultySetting = EnumDifficulty.getDifficultyEnum(difficulty);
                    server.setAllowedSpawnTypes(meta.isSpawnHostileMobs() && difficulty > 0, meta.isSpawnFriendlyMobs());
                }else{
                    throw new Exception("Excpected 1 int argument");
                }
                break;
            case 11: //sendTimeUpdate
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.getGameManager().setCountdownMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 12: //setTime
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.map.getWorld().setWorldTime(((Double) arguments[0]).intValue() % 24000);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
        }
        return null;
    }

    private ILuaObject wrapPlayer(final Player player){
        if(player == null){
            return null;
        }
        return new ILuaObject() {
            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "getUsername",
                        "getTeam",
                        "clearInventory",
                        "setSpawn",
                        "setGamemode",
                        "setHealth",
                        "setFood",
                        "setExperience"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getUsername
                        return new Object[]{player.getUsername()};
                    case 1: //getTeam
                        return new Object[]{MapApi.this.wrapTeam(player.getTeam())};
                    case 2: //clearInventory
                        return new Object[]{player.getEntity().inventory.clearInventory(null, -1)};
                    case 3: //setSpawn
                        if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue());
                            player.setSpawnpoint(spawn);
                        }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue(), ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                            player.setSpawnpoint(spawn);
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                    case 4: //setGamemode
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.setGameMode(Gamemode.fromId(((Double) arguments[0]).intValue()));
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                    case 5: //setHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.getEntity().setHealth(((Double) arguments[0]).floatValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 6: //setFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            FoodStats foodStats = player.getEntity().getFoodStats();
                            foodStats.addStats(((Double) arguments[0]).intValue() - foodStats.getFoodLevel(), 0);
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 7: //setExperience
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.getEntity().experienceLevel = ((Double) arguments[0]).intValue();
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                }
                return null;
            }
        };
    }

    private ILuaObject wrapTeam(final Team team){
        if(team == null){
            return null;
        }
        return new ILuaObject() {
            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "getName",
                        "getPlayers",
                        "forEachPlayer",
                        "setSpawn"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getName
                        return new Object[]{team.getName()};
                    case 1: //getPlayers
                        List<Player> players = team.getMembers();
                        java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                        for(int i = 0; i < players.size(); i++){
                            table.put(i + 1, MapApi.this.wrapPlayer(players.get(i)));
                        }
                        return new Object[]{table};
                    case 2: //forEachPlayer
                        if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                            LuaClosure closure = (LuaClosure) arguments[0];
                            LuaMachine machine = MapApi.this.env.getMachine().getLuaMachine();
                            List<Player> players1 = team.getMembers();
                            for(int i = 0; i < players1.size(); i++){
                                closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{MapApi.this.wrapPlayer(players1.get(i))}, 0)));
                            }
                        }else{
                            throw new Exception("Excpected 1 function as argument");
                        }
                        break;
                    case 3: //setSpawn
                        if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue());
                            team.setSpawnpoint(spawn);
                        }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue(), ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                            team.setSpawnpoint(spawn);
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                }
                return null;
            }
        };
    }
}
