package jk_5.nailed.map.script.api;

import com.google.gson.*;

import org.luaj.vm2.*;

import net.minecraft.world.*;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.map.teleport.Teleporter;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.script.*;
import jk_5.nailed.map.stat.*;
import jk_5.nailed.map.stat.types.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapApi implements ILuaAPI {

    private final IAPIEnvironment env;
    private Map map;

    @java.beans.ConstructorProperties({"env"})
    public MapApi(IAPIEnvironment env) {
        this.env = env;
    }

    @Override
    public String[] getNames() {
        return new String[]{"map"};
    }

    @Override
    public void startup() {
        this.map = NailedAPI.getMapLoader().getMap(env.getMachine().getMachine().getWorld());
    }

    @Override
    public void advance(double paramDouble) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
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
                "setTime",
                "setWinner",
                "disableStat",
                "enableStat",
                "spreadPlayers",
                "tpAllToLobby",
                "remove",
                "recycle",
                "hasMappack",
                "onStarted",
                "onStopped",
                "stopGame",
                "setMaxFood",
                "setMinFood",
                "setMaxHealth",
                "setMinHealth"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method){
            case 0: //sendChatComponent
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
            case 1: //sendChat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.broadcastChatMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 2: //watchUnready
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWatchUnready((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 3: //winnerInterrupt
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWinnerInterrupt((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 4: //getPlayers
                List<Player> players = this.map.getPlayers();
                java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                for(int i = 0; i < players.size(); i++){
                    table.put(i + 1, players.get(i));
                }
                return new Object[]{table};
            case 5: //getTeams
                List<Team> teams = this.map.getTeamManager().getTeams();
                java.util.Map<Integer, ILuaObject> table1 = Maps.newHashMap();
                for(int i = 0; i < teams.size(); i++){
                    table1.put(i + 1, teams.get(i));
                }
                return new Object[]{table1};
            case 6: //forEachPlayer
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Player> players1 = this.map.getPlayers();
                    for(int i = 0; i < players1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{players1.get(i)}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
            case 7: //forEachTeam
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Team> teams1 = this.map.getTeamManager().getTeams();
                    for(int i = 0; i < teams1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{teams1.get(i)}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
            case 8: //getTeam
                if(arguments.length == 1 && arguments[0] instanceof String){
                    return new Object[]{this.map.getTeamManager().getTeam((String) arguments[0])};
                }else{
                    throw new Exception("Excpected 1 string argument");
                }
            case 9: //setDifficulty
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    // MappackMetadata meta = this.map.getMappack().getMappackMetadata();this is not used, and has a possibility of NPE, so i commented it out. TODO: check if needed
                    World server = this.map.getWorld();
                    int difficulty = ((Double) arguments[0]).intValue();
                    server.difficultySetting = EnumDifficulty.getDifficultyEnum(difficulty);
                    server.setAllowedSpawnTypes(difficulty > 0, true);
                }else{
                    throw new Exception("Excpected 1 int argument");
                }
                break;
            case 10: //sendTimeUpdate
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.getGameManager().setCountdownMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 11: //setTime
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.map.getWorld().setWorldTime(((Double) arguments[0]).intValue() % 24000);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 12: //setWinner
                if(arguments.length == 1 && arguments[0] instanceof HashMap){
                    try{
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if("player".equals(type)){
                            String username = obj.get("getUsername").call().checkjstring();
                            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                            if(player == null){
                                throw new Exception("Player " + username + " does not exist");
                            }
                            if(player.getCurrentMap() != this.map){
                                throw new Exception("Player " + username + " is not in this world");
                            }
                            this.map.getGameManager().setWinner(player);
                        }else if("team".equals(type)){
                            String name = obj.get("getID").call().checkjstring();
                            Team team = this.map.getTeamManager().getTeam(name);
                            if(team == null){
                                throw new Exception("Team " + name + " does not exist");
                            }
                            this.map.getGameManager().setWinner(team);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("The object passed is not a team or player");
                    }
                }else{
                    throw new Exception("Expected 1 player/team argument");
                }
                break;
            case 13: //disableStat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    Stat stat = this.map.getStatManager().getStat((String) arguments[0]);
                    if(stat != null && stat instanceof DefaultStat){
                        IStatType type = ((DefaultStat) stat).getType();
                        if(type instanceof StatTypeModifiable){
                            stat.disable();
                        }
                    }
                }
                break;
            case 14: //enableStat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    Stat stat = this.map.getStatManager().getStat((String) arguments[0]);
                    if(stat != null && stat instanceof DefaultStat){
                        IStatType type = ((DefaultStat) stat).getType();
                        if(type instanceof StatTypeModifiable){
                            stat.enable();
                        }
                    }
                }
                break;
            case 15: //spreadPlayers
                for(Player player : this.map.getPlayers()){
                    Location spawn = this.map.getRandomSpawnpoint();
                    if(spawn == null){
                        throw new Exception("No random spawnpoints were found");
                    }
                    player.getEntity().setLocationAndAngles(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
                }
                break;
            case 16: //tpAllToLobby
                TeleportOptions options = NailedAPI.getMapLoader().getLobby().getSpawnTeleport();
                Teleporter teleporter = NailedAPI.getTeleporter();
                for(Player player : this.map.getPlayers()){
                    teleporter.teleportEntity(player.getEntity(), options);
                }
                break;
            case 17: //remove
                break;
            case 18: //recycle
                break;
            case 19: //hasMappack
                return new Object[]{this.map.getMappack() != null};
            case 20: //onStarted
                this.map.getGameManager().onStarted();
                break;
            case 21: //onStopped
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().onStopped((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 22: //stopGame
                this.map.getGameManager().stopGame();
                break;
            case 23: //setMaxFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.map.getPlayers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 24: //setMinFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.map.getPlayers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMinFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 25:
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.map.getPlayers()){
                        player.setMaxHealth(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 26:
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player : this.map.getPlayers()){
                        player.setMinHealth(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
        }
        return null;
    }
}
