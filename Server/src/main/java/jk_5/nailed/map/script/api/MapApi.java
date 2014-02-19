package jk_5.nailed.map.script.api;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.script.*;
import jk_5.nailed.util.Utils;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.IChatComponent;
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

    private static final Joiner argsJoiner = Joiner.on(' ').skipNulls();
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
                "countdown"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0: //sendNotification
                this.map.broadcastNotification(argsJoiner.join(arguments));
            case 1: //sendChatComponent
                try{
                    IChatComponent comp = IChatComponent.Serializer.func_150699_a(argsJoiner.join(arguments));
                    this.map.broadcastChatMessage(comp);
                }catch(JsonParseException e){
                    throw new Exception("Chat message is not of json format");
                }
            case 2: //sendChat
                this.map.broadcastChatMessage(argsJoiner.join(arguments));
            case 3: //watchUnready
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWatchUnready((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
            case 4: //winnerInterrupt
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWinnerInterrupt((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
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
            case 10: //countdown
                if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof String){
                    try{
                        long ellapsed = 0;
                        long seconds = Math.round((Double) arguments[0]);
                        String message = (String) arguments[1];
                        do{
                            this.map.getGameManager().setCountdownMessage(message.replace("%s", Utils.secondsToShortTimeString(seconds - ellapsed)));
                            Thread.sleep(1000);
                            ellapsed ++;
                        }while(ellapsed != seconds);
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception(e);
                    }
                }else{
                    throw new Exception("Excpected 1 int argument and 1 string argument");
                }
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
                        "getTeam"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getUsername
                        return new Object[]{player.getUsername()};
                    case 1: //getTeam
                        return new Object[]{MapApi.this.wrapTeam(player.getTeam())};
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
                        "forEachPlayer"
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
                        }
                }
                return null;
            }
        };
    }
}
