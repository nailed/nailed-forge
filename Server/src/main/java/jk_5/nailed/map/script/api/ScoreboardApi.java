package jk_5.nailed.map.script.api;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.map.scoreboard.ScoreboardManager;
import jk_5.nailed.api.map.scoreboard.ScoreboardTeam;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.map.script.IAPIEnvironment;
import org.luaj.vm2.LuaFunction;

import java.util.HashMap;

/**
 * No description given
 *
 * @author jk-5
 */
public class ScoreboardApi implements ILuaAPI {

    private final IAPIEnvironment env;
    private ScoreboardManager manager;

    @java.beans.ConstructorProperties({"env"})
    public ScoreboardApi(IAPIEnvironment env) {
        this.env = env;
    }

    @Override
    public String[] getNames(){
        return new String[]{"scoreboard"};
    }

    @Override
    public void startup(){
        this.manager = NailedAPI.getMapLoader().getMap(env.getMachine().getMachine().getWorld()).getScoreboardManager();
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
                    return new Object[]{objective};
                }else{
                    throw new Exception("Expected 1 string argument");
                }
            case 1: //getTeam
                if(arguments.length == 1 && arguments[0] instanceof String){
                    ScoreboardTeam team = this.manager.getOrCreateTeam((String) arguments[0]);
                    return new Object[]{team};
                }else{
                    throw new Exception("Expected 1 string argument");
                }
            case 2: //setDisplay
                if(arguments.length == 2 && arguments[0] instanceof HashMap && arguments[1] instanceof String){
                    try{
                        //noinspection unchecked
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if(type.equals("objective")){
                            String id = obj.get("getId").call().checkjstring();
                            Objective objective = this.manager.getObjective(id);
                            if(objective == null){
                                throw new Exception("Objective " + id + " does not exist");
                            }
                            DisplayType typ;
                            String inType = (String) arguments[1];
                            if(inType.equalsIgnoreCase("list")){
                                typ = DisplayType.PLAYER_LIST;
                            }else if(inType.equalsIgnoreCase("belowName")){
                                typ = DisplayType.BELOW_NAME;
                            }else if(inType.equalsIgnoreCase("sidebar")){
                                typ = DisplayType.SIDEBAR;
                            }else{
                                throw new Exception("Unknown display type: " + inType);
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
}
