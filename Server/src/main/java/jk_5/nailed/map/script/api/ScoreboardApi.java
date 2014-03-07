package jk_5.nailed.map.script.api;

import com.google.common.collect.Maps;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.ScoreboardManager;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.script.*;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class ScoreboardApi implements ILuaAPI {

    private final IAPIEnvironment env;
    private Map map;
    private ScoreboardManager manager;

    @Override
    public String[] getNames(){
        return new String[]{"scoreboard"};
    }

    @Override
    public void startup(){
        this.map = NailedAPI.getMapLoader().getMap(env.getMachine().getMachine().getWorld());
        this.manager = this.map.getScoreboardManager();
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
                "getObjective",
                "getTeam",
                "setDisplay"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0: //getObjective
                if(arguments.length == 1 && arguments[0] instanceof String){
                    Objective objective = this.manager.getOrCreateObjective((String) arguments[0]);
                    return new Object[]{this.wrapObjective(objective)};
                }else{
                    throw new Exception("Expected 1 string argument");
                }
            case 1: //getTeam
                if(arguments.length == 1 && arguments[0] instanceof String){
                    ScoreboardTeam team = this.manager.getOrCreateTeam((String) arguments[0]);
                    return new Object[]{this.wrapTeam(team)};
                }else{
                    throw new Exception("Expected 1 string argument");
                }
            case 2: //setDisplay
                if(arguments.length == 2 && arguments[0] instanceof HashMap && arguments[1] instanceof String){
                    try{
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if(type.equals("objective")){
                            String id = obj.get("getId").call().checkjstring();
                            Objective objective = this.manager.getObjective(id);
                            if(objective == null){
                                throw new Exception("Objective " + id + " does not exist");
                            }
                            DisplayType typ = null;
                            String inType = (String) arguments[1];
                            if(inType.equalsIgnoreCase("list")){
                                typ = DisplayType.PLAYER_LIST;
                            }else if(inType.equalsIgnoreCase("belowName")){
                                typ = DisplayType.BELOW_NAME;
                            }else if(inType.equalsIgnoreCase("sidebar")){
                                typ = DisplayType.SIDEBAR;
                            }
                            this.manager.setDisplay(typ, objective);
                            return new Object[]{};
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("The object passed is not an objective");
                    }
                }else{
                    throw new Exception("Expected 1 objective and 1 string argument");
                }
                break;
        }
        return new Object[0];
    }

    private ILuaObject wrapObjective(final Objective objective){
        return new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "getId",
                        "getDisplayName",
                        "setDisplayName",
                        "getScore",
                        "setScore",
                        "addScore",
                        "getType"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getId
                        return new Object[]{objective.getId()};
                    case 1: //getDisplayName
                        return new Object[]{objective.getDisplayName()};
                    case 2: //setDisplayName
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            objective.setDisplayName((String) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                        break;
                    case 3: //getScore
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            return new Object[]{objective.getScore((String) arguments[0]).getValue()};
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                    case 4: //setScore
                        if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
                            int score = ((Double) arguments[1]).intValue();
                            objective.getScore((String) arguments[0]).setValue(score);
                        }else{
                            throw new Exception("Expected 1 string and 1 int argument");
                        }
                        break;
                    case 5: //addScore
                        if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
                            int score = ((Double) arguments[1]).intValue();
                            objective.getScore((String) arguments[0]).addValue(score);
                        }else{
                            throw new Exception("Expected 1 string and 1 int argument");
                        }
                        break;
                    case 6: //getType
                        return new Object[]{"objective"};
                }
                return new Object[0];
            }
        };
    }

    private ILuaObject wrapTeam(final ScoreboardTeam team){
        return new ILuaObject() {

            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "getId",
                        "getDisplayName",
                        "setDisplayName",
                        "getType",
                        "getPrefix",
                        "setPrefix",
                        "getSuffix",
                        "setSuffix",
                        "isFriendlyFire",
                        "setFriendlyFire",
                        "isFriendlyInvisiblesVisible",
                        "setFriendlyInvisiblesVisible",
                        "addPlayer",
                        "removePlayer",
                        "getPlayers",
                        "forEachPlayer"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getId
                        return new Object[]{team.getId()};
                    case 1: //getDisplayName
                        return new Object[]{team.getDisplayName()};
                    case 2: //setDisplayName
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            team.setDisplayName((String) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                        break;
                    case 3: //getType
                        return new Object[]{"scoreboardTeam"};
                    case 4: //getPrefix
                        return new Object[]{team.getPrefix()};
                    case 5: //setPrefix
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            team.setPrefix((String) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                        break;
                    case 6: //getSuffix
                        return new Object[]{team.getSuffix()};
                    case 7: //setSuffix
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            team.setSuffix((String) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                        break;
                    case 8: //isFriendlyFire
                        return new Object[]{team.isFriendlyFire()};
                    case 9: //setFriendlyFire
                        if(arguments.length == 1 && arguments[0] instanceof Boolean){
                            team.setFriendlyFire((Boolean) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 boolean argument");
                        }
                        break;
                    case 10: //isFriendlyInvisiblesVisible
                        return new Object[]{team.isFriendlyInvisiblesVisible()};
                    case 11: //setFriendlyInvisiblesVisible
                        if(arguments.length == 1 && arguments[0] instanceof Boolean){
                            team.setFriendlyInvisiblesVisible((Boolean) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 boolean argument");
                        }
                        break;
                    case 12: //addPlayer
                        if(arguments.length == 1 && arguments[0] instanceof HashMap){
                            try{
                                HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                                String type = obj.get("getType").call().checkjstring();
                                if(type.equals("player")){
                                    String username = obj.get("getUsername").call().checkjstring();
                                    Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                                    if(player == null){
                                        throw new Exception("Player " + username + " does not exist");
                                    }
                                    if(player.getCurrentMap() != map){
                                        throw new Exception("Player " + username + " is not in this world");
                                    }
                                    return new Object[]{team.addPlayer(player)};
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                throw new Exception("The object passed is not a player");
                            }
                        }else{
                            throw new Exception("Expected 1 player argument");
                        }
                        break;
                    case 13: //removePlayer
                        if(arguments.length == 1 && arguments[0] instanceof HashMap){
                            try{
                                HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                                String type = obj.get("getType").call().checkjstring();
                                if(type.equals("player")){
                                    String username = obj.get("getUsername").call().checkjstring();
                                    Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                                    if(player == null){
                                        throw new Exception("Player " + username + " does not exist");
                                    }
                                    if(player.getCurrentMap() != map){
                                        throw new Exception("Player " + username + " is not in this world");
                                    }
                                    return new Object[]{team.removePlayer(player)};
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                throw new Exception("The object passed is not a player");
                            }
                        }else{
                            throw new Exception("Expected 1 player argument");
                        }
                        break;
                    case 14: //getPlayers
                        Set<Player> players = team.getPlayers();
                        java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                        int i = 1;
                        for(Player player : players){
                            table.put(i, MapApi.wrapPlayer(env, player));
                            i++;
                        }
                        return new Object[]{table};
                    case 15: //forEachPlayer
                        if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                            LuaClosure closure = (LuaClosure) arguments[0];
                            LuaMachine machine = env.getMachine().getLuaMachine();
                            Set<Player> players1 = team.getPlayers();
                            for(Player player : players1){
                                closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{MapApi.wrapPlayer(env, player)}, 0)));
                            }
                        }else{
                            throw new Exception("Excpected 1 function as argument");
                        }
                        break;
                }
                return new Object[0];
            }
        };
    }
}
