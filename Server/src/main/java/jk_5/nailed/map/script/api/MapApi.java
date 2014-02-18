package jk_5.nailed.map.script.api;

import com.google.common.base.Joiner;
import com.google.gson.JsonParseException;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.script.IAPIEnvironment;
import jk_5.nailed.map.script.ILuaAPI;
import jk_5.nailed.map.script.ILuaContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.IChatComponent;

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
                "getPlayers"
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
                return players.toArray(new Object[players.size()]);
        }
        return null;
    }
}
