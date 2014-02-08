package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandLobby extends NailedCommand {

    public CommandLobby(){
        super("lobby");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        sender.teleportToMap(NailedAPI.getMapLoader().getLobby());
    }
}
