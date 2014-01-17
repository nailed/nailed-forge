package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandLobby extends NailedCommand {

    @Override
    public String getCommandName(){
        return "lobby";
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        sender.teleportToMap(MapLoader.instance().getLobby());
    }
}
